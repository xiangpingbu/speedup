# coding=utf-8
from rest.app_base import *
from service.db import tool_model_service

base = '/tool'


@app.route(base + "/init_model_name", methods=['GET'])
def init_model_name():
    """
    training文件变量初始化页面,将会初始化各个模型的条目供用户选择
    :return: [model_name1,model_name2,model_name3]
    """
    result = tool_model_service.load_model(is_deleted=0)
    # only get  model_name form result
    result = list(map(lambda x: x["model_name"], result))

    responseto(result)


@app.route(base + "/get_branch_name", methods=['GET'])
def get_branch_name():
    """
    根据模型名称获得分支信息和文件路径
    :param: model_name
    :return: model_name file_payj
    """
    model_name = request.form.get("modelName")
    result = tool_model_service.load_model(model_name=model_name)
    #get model_branch and file_path from result
    result = list(map(lambda x: {"model_branch":x["model_branch"],"file_path":x["file_path"]}, result))

    responseto(result)

'''
创建新的分支,将会复制原有的分支的内容
'''
@app.route(base + "/branch", methods=['POST'])
def new_branch():
    model_name = request.form.get("model_name")
    branch = request.form.get("branch")
    original_branch = request.form.get("original_branch")

    result = tool_model_service.load_binning_record(model_name, original_branch)

    list = []

    for record in result:
        obj = [model_name, branch, record["variable_name"], record["variable_iv"], record["binning_record"],record["is_selected"]]
        list.append(obj)

    if tool_model_service.copy_branch(model_name, branch,original_branch):
        tool_model_service.save_binning_record(list)
        return responseto(data=True)
    return responseto(data=False)
