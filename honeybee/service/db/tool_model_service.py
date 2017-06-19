# coding=utf-8
from common.db import Sql_util as util
from common.util import db_util
from common.db_orm import sql_util
from datetime import datetime
from beans.tool_model import *


def load_model(**params):
    """获得对应模型的信息:分支,模型名称,资源文件位置等"""

    # sql = "select model_name,model_branch,model_target,remove_list,selected_list,file_path, " \
    #       "create_date,modify_date,is_deleted from tool_model"
    #
    # process_result = db_util.process_query(sql, params)
    #
    #
    # result = util.query(process_result[0], process_result[1])

    # criterion = map(lambda x: Model.__dict__[x] == params[x], params)
    # criterion.append(Model.is_deleted == '0')
    params['is_deleted'] = params['is_deleted'] if params.__contains__('is_deleted') else '0'
    result = sql_util.query(Model, **params)
    return result


def update_branch(name, branch, target, remove_list=None, selected_list=None, file_path=None):
    '''更新对应分支的配置'''
    # sql = "update tool_model set model_target= %s , remove_list = %s , selected_list = %s ,file_path = %s, modify_date= %s where model_name=%s and model_branch= %s"
    # result = util.execute(sql, (target, remove_list, selected_list,file_path, datetime.now(), name, branch))
    update = {Model.model_target: target,
              Model.remove_list: remove_list,
              Model.selected_list: selected_list,
              Model.file_path: file_path,
              Model.modify_date: datetime.now()}
    num = sql_util.update_dict(Model, update, model_name = name, model_branch = branch)

    if num > 0:
        return True
    return False


def copy_branch(name, branch, original_branch):
    record = load_model(model_name=name, model_branch=original_branch)[0]
    return create_branch(model_name=name,
                         model_branch=branch,
                         model_target=record["model_target"],
                         remove_list=record["remove_list"],
                         selected_list=record["selected_list"])


# def create_branch(name, branch, target=None, remove_list=None, selected_list=None):
def create_branch(**params):
    '''获得对应模型的所有分支'''
    # json_remove_list = None if remove_list is None else json.dumps(remove_list, ensure_ascii=False)
    # selected_list = None if selected_list is None else json.dumps(selected_list, ensure_ascii=False)
    # now = datetime.now()

    # process_insert会根据参数拼接sql
    # sql = "insert into tool_model"
    # params["create_date"] = datetime.now()
    # params["modify_date"] = datetime.now()
    #
    # process_result = db_util.process_insert(sql,params)
    #
    # result = util.execute(process_result[0], process_result[1])

    model = Model()
    for x in params:
        model.__dict__[x] = params[x]

    result = sql_util.add_all(model)

    if len(result) > 0:
        return True
    return False


def load_binning_record(model_name, model_branch, variables=None):
    criterion = []
    criterion.append(ModelContent.model_name == model_name)
    criterion.append(ModelContent.model_branch == model_branch)
    criterion.append(ModelContent.is_deleted == '0')
    if variables is not None:
        criterion.append(ModelContent.variable_name.in_(variables))

    records = sql_util.query(ModelContent, *criterion)

    return records


def save_binning_record(variable_list):
    results = sql_util.add_all(variable_list)
    if len(results) > 0:
        return True
    return False


def del_binning_record(model_name, model_branch):
    update = {ModelContent.is_deleted: '1',
              ModelContent.modify_date: datetime.now()}
    num = sql_util.update_dict(ModelContent, update,
                               model_name = model_name,
                               model_branch = model_branch)
    if num > 0:
        return True
    return False


def save_selected_variable(model_name, model_branch, var_list):
    model = ModelSelectedVariable(model_name=model_name,
                                  model_branch=model_branch,
                                  selected_variable=var_list)

    # sql = "insert into tool_model_selected_variable(model_name,model_branch,selected_variable,modify_date,create_date) values(%s,%s,%s,now(),now())"
    # result = util.execute(sql, (model_name, model_branch, var_list))
    result = sql_util.add_all(model)

    if len(result) > 0:
        return True
    else:
        return False


def del_selected_variable(model_name, model_branch):
    if (len(get_selected_variable(model_name, model_branch)) > 0):
        # sql = "update tool_model_selected_variable " \
        #       "set is_deleted=1 ,modify_date=now()" \
        #       "where model_name = %s and model_branch=%s"
        update = {ModelSelectedVariable.is_deleted :'1',
                  ModelSelectedVariable.modify_date : datetime.now(),
                  }
        # result = util.execute(sql, (model_name, model_branch))
        num = sql_util.update_dict(ModelSelectedVariable,
                                   update,
                                   model_branch = model_branch,
                                   model_name = model_name)
        if num > 0:
            return True
        else:
            return False
    else:
        return True

def get_selected_variable(model_name, model_branch):
    results = sql_util.query(ModelSelectedVariable,
                            model_name = model_name,
                            model_branch = model_branch,
                            is_deleted='0')
    return results

# results = get_selected_variable("model_data","master")
# for result in results:
#     print result.id

