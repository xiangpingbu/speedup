# coding=utf-8
# from common import sql_util
from common.db import Sql_util as util
import json

from datetime import  datetime

def load_model(name):
    '''获得对应模型的所有分支'''
    sql = "select model_branch,remove_list from tool_model where model_name = %s and is_deleted = 0"
    result = util.query(sql,(name))
    return result

def load_branch(name,branch):
    sql = "select model_target,remove_list,selected_list from tool_model where model_name = %s and model_branch= %s"
    result = util.query(sql,(name,branch))
    return result



def create_branch(name,branch,target = None,remove_list = None,selected_list = None):
    '''获得对应模型的所有分支'''
    json_remove_list = None if remove_list is None else json.dumps(remove_list,ensure_ascii=False)
    selected_list = None if selected_list is None else json.dumps(selected_list,ensure_ascii=False)
    now = datetime.now()

    sql = "insert into tool_model(model_name,model_branch,model_target,remove_list," \
          "selected_list,create_date,modify_date) VALUES(%s,%s,%s,%s,%s,%s,%s)"
    result = util.execute(sql,(name,branch,target,json_remove_list,selected_list,now,now))
    if result > 0 :
        return True
    return False

def update_branch(name,branch,target,remove_list = None,selected_list = None):
    '''更新对应分支的配置'''
    json_remove_list = None if remove_list is None else json.dumps(remove_list,ensure_ascii=False)
    selected_list = None if selected_list is None else json.dumps(selected_list,ensure_ascii=False)

    sql = "update tool_model set model_target= %s , remove_list = %s , selected_list = %s , modify_date= %s where model_name=%s and model_branch= %s"
    result = util.execute(sql,(target,json_remove_list,selected_list,datetime.now(),name,branch))
    if result > 0 :
        return True
    return False

def if_branch_exist(name,branch):
    sql = "select count(1) from tool_model where model_name = %s and model_branch = %s"
    result = util.execute(sql,(name,branch))
    if result > 0:
        return True
    return False

