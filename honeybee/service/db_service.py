# coding=utf-8
# from common import sql_util
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
    # json_remove_list = None if remove_list is None else json.dumps(remove_list, ensure_ascii=False)
    # selected_list = None if selected_list is None else json.dumps(selected_list, ensure_ascii=False)
    now = datetime.now()

    sql = "insert into tool_model(model_name,model_branch,model_target,remove_list," \
          "selected_list,create_date,modify_date) VALUES(%s,%s,%s,%s,%s,%s,%s)"
    result = util.execute(sql, (name, branch, target, remove_list, selected_list, now, now))
    if result > 0:
        return True
    return False


def copy_branch(name, branch, original_branch):
    record = load_branch(name, original_branch)[0]
    return create_branch(name, branch, record["model_target"], record["remove_list"], record["selected_list"])


def update_branch(name, branch, target, remove_list=None, selected_list=None):
    '''更新对应分支的配置'''
    # json_remove_list = None if remove_list is None else json.dumps(remove_list, ensure_ascii=False)
    # selected_list = None if selected_list is None else json.dumps(selected_list, ensure_ascii=False)

    sql = "update tool_model set model_target= %s , remove_list = %s , selected_list = %s , modify_date= %s where model_name=%s and model_branch= %s"
    result = util.execute(sql, (target, remove_list, selected_list, datetime.now(), name, branch))
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
          "(model_name,model_branch,variable_name,variable_iv,binning_record,is_selected,create_date,modify_date)" \
          " VALUES (%s,%s,%s,%s,%s,%s,now(),now())"
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


'''
读取分bin后的数据
'''


def load_binning_record(model_name, model_branch, variables=None):
    sql = "select id,model_name,model_branch,variable_name,variable_iv,binning_record,is_selected " \
          "from tool_model_content where model_name= %s and model_branch = %s and is_deleted = 0"

    paramList = [model_name, model_branch]

    if variables is not None:
        s = " and variable_name IN (%s)"
        in_p = ', '.join((map(lambda x: '%s', variables)))
        s = s % in_p
        sql += s
        paramList.extend(variables)

    result = util.query(sql, paramList)
    return result


def save_selected_variable(model_name, model_branch, var_list):
    sql = "insert into tool_model_selected_variable(model_name,model_branch,selected_variable,modify_date,create_date) values(%s,%s,%s,now(),now())"
    result = util.execute(sql, (model_name, model_branch, var_list))
    if result > 0:
        return True
    else:
        return False


def update_selected_variable(model_name, model_branch, var_list, modify_date, is_deleted=0):
    sql = "update tool_model_selected_variable " \
          "set var_list = %s,modify_date=now(),is_deleted=%s " \
          "where model_name = %s and model_branch=%s"
    result = util.execute(sql, {var_list, is_deleted, model_name, model_branch})
    if result > 0:
        return True
    else:
        return False


def del_selected_variable(model_name, model_branch):
    if (len(get_selected_variable(model_name, model_branch)) > 0):
        sql = "update tool_model_selected_variable " \
              "set is_deleted=1 ,modify_date=now()" \
              "where model_name = %s and model_branch=%s"
        result = util.execute(sql, (model_name, model_branch))
        if result > 0:
            return True
        else:
            return False
    else:
        return True


def get_selected_variable(model_name, model_branch):
    sql = "select id,model_name,model_branch,selected_variable,modify_date,create_date from tool_model_selected_variable " \
          "where model_name=%s and model_branch=%s"

    return util.query(sql, (model_name, model_branch))
