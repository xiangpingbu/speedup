#!/usr/bin/env python

DOCUMENTATION = """
"""

EXAMPLES = """
# basic pre_task and post_task example
pre_tasks:
  - name: Gathering ec2 facts
    action: ec2_facts
  - name: Instance De-register
    local_action:
      module: ec2_elb
      instance_id: "{{ ansible_ec2_instance_id }}"
      state: absent
roles:
  - myrole
post_tasks:
  - name: Instance Register
    local_action:
      module: ec2_elb
      instance_id: "{{ ansible_ec2_instance_id }}"
      ec2_elbs: "{{ item }}"
      state: present
    with_items: ec2_elbs
"""

import sys
import time
import json

try:
    from aliyunsdkcore import client
    from aliyunsdkslb.request.v20140515 import AddBackendServersRequest
    from aliyunsdkslb.request.v20140515 import RemoveBackendServersRequest
    from aliyunsdkslb.request.v20140515 import DescribeHealthStatusRequest
    HAS_ALIYUN_SDK = True
except ImportError:
    HAS_ALIYUN_SDK = False


class ElbManager:
    """Handles EC2 instance ELB registration and de-registration"""

    def __init__(self, module, instance_id=None, lb_id=None,
                 region=None, weight=None):
        self.module = module
        self.instance_id = instance_id
        self.region = region
        self.weight = weight
        self.lb_id = lb_id
        self.changed = False
        self.client = client.AcsClient('LTAIpdPQ42JdWhLz', '9H3tipPzP3Geten7VAvVKgcqc8bSTv', region)

    def deregister(self, wait, timeout):
        """De-register the instance from all ELBs and wait for the ELB
        to report it out-of-service"""

        initial_state = self._get_instance_health()
        if initial_state not in ['normal', 'abnormal']:
            return

        request = RemoveBackendServersRequest.RemoveBackendServersRequest()
        request.set_accept_format('json')
        request.set_LoadBalancerId(self.lb_id)
        request.set_BackendServers(json.dumps([self.instance_id]))
        try:
            self.client.do_action_with_exception(request)
            self.changed = True
            if wait:
                self._await_elb_instance_state('OutOfService', initial_state, timeout)
        except:
            print('Unexpected error:', sys.exc_info()[0])

    def register(self, wait, timeout):
        """Register the instance for all ELBs and wait for the ELB
        to report the instance in-service"""
        initial_state = self._get_instance_health()
        if initial_state != 'OutOfService':
            return

        request = AddBackendServersRequest.AddBackendServersRequest()
        request.set_accept_format('json')
        request.set_LoadBalancerId(self.lb_id)
        request.set_BackendServers(json.dumps([{'ServerId': self.instance_id, 'Weight': self.weight}]))
        try:
            self.client.do_action_with_exception(request)
            self.changed = True
            if wait:
                self._await_elb_instance_state('normal', initial_state, timeout)
        except:
            print('Unexpected error:', sys.exc_info()[0])

    def _await_elb_instance_state(self, awaited_state, initial_state, timeout):
        """Wait for an ELB to change state
        awaited_state : state to poll for (string)"""

        wait_timeout = time.time() + timeout
        while True:
            instance_state = self._get_instance_health()

            if not instance_state:
                msg = ("The instance %s could not be put in service on %s."
                       " Reason: Invalid Instance")
                self.module.fail_json(msg=msg % (self.instance_id, self.lb_id))

            if instance_state == awaited_state:
                # Check the current state against the initial state, and only set
                # changed if they are different.
                if (initial_state is None) or (instance_state != initial_state):
                    self.changed = True
                break
            elif time.time() >= wait_timeout:
                msg = ("The instance %s could not be put in service on %s."
                       " Reason: Unavailable")
                self.module.fail_json(msg=msg % (self.instance_id, self.lb_id))
            time.sleep(1)

    def _get_instance_health(self):
        """
        Check instance health, should return normal/abnormal/unavailable or None under
        certain error conditions.
        """
        request = DescribeHealthStatusRequest.DescribeHealthStatusRequest()
        request.set_accept_format('json')
        request.set_LoadBalancerId(self.lb_id)
        try:
            response = self.client.do_action_with_exception(request)
            jres = json.loads(response)

            for server in jres['BackendServers']['BackendServer']:
                if server['ServerId'] == self.instance_id:
                    return server['ServerHealthStatus']
        except:
            print('Unexpected error:', sys.exc_info()[0])
            return None

        return 'OutOfService'


def main():
    argument_spec = dict(
        state={'required': True},
        instance_id={'required': True},
        lb_id={'required': True},
        region={'required': True},
        weight={'required': True, 'type': 'int'},
        wait={'required': False, 'default': True, 'type': 'bool'},
        wait_timeout={'required': False, 'default': 0, 'type': 'int'},
    )

    module = AnsibleModule(
        argument_spec=argument_spec,
    )

    if not HAS_ALIYUN_SDK:
        module.fail_json(msg='aliyun sdk required for this module')

    for param in ('instance_id', 'lb_id', 'region', 'weight'):
        if param not in module.params:
            module.fail_json(msg= param + " are required for registration")

    instance_id = module.params['instance_id']
    lb_id = module.params['lb_id']
    region = module.params['region']
    weight = module.params['weight']
    wait = module.params['wait']
    timeout = module.params['wait_timeout']

    instance_id = module.params['instance_id']
    elb_man = ElbManager(module, instance_id, lb_id, region, weight)

    if module.params['state'] == 'present':
        elb_man.register(wait, timeout)
    elif module.params['state'] == 'absent':
        elb_man.deregister(wait, timeout)

    ansible_facts = {'lb_id': lb_id}
    ec2_facts_result = dict(changed=elb_man.changed, ansible_facts=ansible_facts)

    module.exit_json(**ec2_facts_result)

# import module snippets
from ansible.module_utils.basic import *

if __name__ == '__main__':
    main()
