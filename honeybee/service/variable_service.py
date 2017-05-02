# coding=utf-8
# from common import sql_util
import json
from datetime import datetime

from common.db import Sql_util as util


def load_model(name):
    '''获得对应模型的所有分支'''
    sql = "select model_branch,remove_list,model_target from tool_model where model_name = %s and is_deleted = 0"
    result = util.query(sql, [name])
    return result


def load_branch(name, branch):
    sql = "select model_target,remove_list,selected_list from tool_model where model_name = %s and model_branch= %s"
    result = util.query(sql, (name, branch))
    return result


def create_branch(name, branch, target=None, remove_list=None, selected_list=None):
    '''获得对应模型的所有分支'''
    json_remove_list = None if remove_list is None else json.dumps(remove_list, ensure_ascii=False)
    selected_list = None if selected_list is None else json.dumps(selected_list, ensure_ascii=False)
    now = datetime.now()

    sql = "insert into tool_model(model_name,model_branch,model_target,remove_list," \
          "selected_list,create_date,modify_date) VALUES(%s,%s,%s,%s,%s,%s,%s)"
    result = util.execute(sql, (name, branch, target, json_remove_list, selected_list, now, now))
    if result > 0:
        return True
    return False


def update_branch(name, branch, target, remove_list=None, selected_list=None):
    '''更新对应分支的配置'''
    json_remove_list = None if remove_list is None else json.dumps(remove_list, ensure_ascii=False)
    selected_list = None if selected_list is None else json.dumps(selected_list, ensure_ascii=False)

    sql = "update tool_model set model_target= %s , remove_list = %s , selected_list = %s , modify_date= %s where model_name=%s and model_branch= %s"
    result = util.execute(sql, (target, json_remove_list, selected_list, datetime.now(), name, branch))
    if result > 0:
        return True
    return False


def if_branch_exist(name, branch):
    sql = "select count(1) from tool_model where model_name = %s and model_branch = %s"
    result = util.execute(sql, (name, branch))
    if result > 0:
        return True
    return False


def save_binning_record(variable_list):
    sql = "insert into tool_model_content " \
          "(model_name,model_branch,variable_name,variable_iv,binning_record,create_date,modify_date)" \
          " VALUES (%s,%s,%s,%s,%s,now(),now())"
    result = util.executmany(sql, variable_list)
    if result > 0:
        return True
    return False


def del_binnbing_record(model_name, model_branch):
    sql = "update tool_model_content set is_deleted=1 , modify_date = now()  where is_deleted =0 and model_name=%s and model_branch = %s  "
    result = util.execute(sql, (model_name, model_branch))
    if result > 0:
        return True
    return False


def load_binning_record(model_name, model_branch):
    sql = "select id,model_name,model_branch,variable_name,variable_iv,binning_record " \
          "from tool_model_content where model_name= %s and model_branch = %s and is_deleted = 0"
    result = util.query(sql, (model_name, model_branch))
    return result

result = load_binning_record("model_train_selected","master")
print 123