#!/bin/bash

Jetty_Home=/homes/maas/jetty-distribution-9.4.1.v20170120
RESOURCE_PATH=./maas-web/target/maas-web.war
SERVER_IP=101.71.245.166



tmp_kill_proc_sh="_killprocess.sh"
scp ./kill_proc.sh $SERVER_IP:"~/$tmp_kill_proc_sh"

#mvn clean package
scp $RESOURCE_PATH $SERVER_IP:~/ROOT.war
ssh -Tq $SERVER_IP << remotessh
    ~/$tmp_kill_proc_sh
    rm -rf ~/$tmp_kill_proc_sh
    rm -rf ${Jetty_Home}/ROOT.war
    mv ~/ROOT.war ${Jetty_Home}/webapps
remotessh





