# coding=utf-8
from common.db import Sql_util as util
from common.util import db_util
from datetime import datetime




def load_model(**params):
    """获得对应模型的信息:分支,模型名称,资源文件位置等"""
    # sql = "select model_branch,remove_list,model_target from tool_model where model_name = %s and is_deleted = 0"

    sql = "select model_name,model_branch,model_target,remove_list,selected_list,file_path, " \
          "create_date,modify_date,is_deleted from tool_model"

    process_result = db_util.process_query(sql, params)

    result = util.query(process_result[0], process_result[1])
    return result

def update_branch(name, branch, target, remove_list=None, selected_list=None,file_path=None):
    '''更新对应分支的配置'''
    sql = "update tool_model set model_target= %s , remove_list = %s , selected_list = %s ,file_path = %s, modify_date= %s where model_name=%s and model_branch= %s"
    result = util.execute(sql, (target, remove_list, selected_list,file_path, datetime.now(), name, branch))
    if result > 0:
        return True
    return False



def copy_branch(name, branch, original_branch):
    record = load_model(model_name = name, model_branch = original_branch)[0]
    return create_branch(model_name=name,
                         model_branch = branch,
                         model_target = record["model_target"],
                         remove_list = record["remove_list"],
                         selected_list = record["selected_list"])

# def create_branch(name, branch, target=None, remove_list=None, selected_list=None):
def create_branch(**params):
    '''获得对应模型的所有分支'''
    # json_remove_list = None if remove_list is None else json.dumps(remove_list, ensure_ascii=False)
    # selected_list = None if selected_list is None else json.dumps(selected_list, ensure_ascii=False)
    now = datetime.now()

    #process_insert会根据参数拼接sql
    sql = "insert into tool_model"
    params["create_date"] = datetime.now()
    params["modify_date"] = datetime.now()

    process_result = db_util.process_insert(sql,params)

    result = util.execute(process_result[0], process_result[1])
    if result > 0:
        return True
    return False


def load_binning_record(model_name, model_branch, variables=None):
    sql = "select id,model_name,model_branch,variable_name,variable_iv,binning_record,is_selected " \
          "from tool_model_content where model_name= %s and model_branch = %s and is_deleted = 0"
    """
    """
    paramList = [model_name, model_branch]

    if variables is not None:
        s = " and variable_name IN (%s)"
        in_p = ', '.join((map(lambda x: '%s', variables)))
        s = s % in_p
        sql += s
        paramList.extend(variables)

    result = util.query(sql, paramList)
    return result

def save_binning_record(variable_list):
    sql = "insert into tool_model_content " \
          "(model_name,model_branch,variable_name,variable_iv,binning_record,is_selected,create_date,modify_date)" \
          " VALUES (%s,%s,%s,%s,%s,%s,now(),now())"
    result = util.executmany(sql, variable_list)
    if result > 0:
        return True
    return False

def del_binning_record(model_name, model_branch):
    sql = "update tool_model_content set is_deleted=1 , modify_date = now()  where is_deleted =0 and model_name=%s and model_branch = %s  "
    result = util.execute(sql, (model_name, model_branch))
    if result > 0:
        return True
    return False

def save_selected_variable(model_name, model_branch, var_list):
    sql = "insert into tool_model_selected_variable(model_name,model_branch,selected_variable,modify_date,create_date) values(%s,%s,%s,now(),now())"
    result = util.execute(sql, (model_name, model_branch, var_list))
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

def update_selected_variable(model_name, model_branch, var_list, modify_date, is_deleted=0):
    sql = "update tool_model_selected_variable " \
          "set var_list = %s,modify_date=now(),is_deleted=%s " \
          "where model_name = %s and model_branch=%s"
    result = util.execute(sql, {var_list, is_deleted, model_name, model_branch})
    if result > 0:
        return True
    else:
        return False

def get_selected_variable(model_name, model_branch):
    sql = "select id,model_name,model_branch,selected_variable,modify_date,create_date from tool_model_selected_variable " \
          "where model_name=%s and model_branch=%s"

    return util.query(sql, (model_name, model_branch))