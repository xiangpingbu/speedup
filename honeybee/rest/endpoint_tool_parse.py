# coding=utf-8
from rest.app_base import *
from service.db import tool_model_service
from common.util import common_util
import pandas as pd
from util import common as cmm
from common import global_value
from service import basic_analysis_service as ba
from datetime import datetime

base = '/tool'


@app.route(base + "/parse", methods=['POST'])
def parse():
    # 对train文件进行转换,分析
    model_name = request.form.get("modelName")
    branch = request.form.get("branch")
    # 用户指定的文件相对路径
    file_path = request.form.get("filePath")
    target = request.form.get("target")
    root_path = app.config["ROOT_PATH"]

    path = root_path + "/" + file_path
    # 以模型名称和分支名作为唯一的key
    key = model_name + "_" + branch
    # df_train = None
    # 流程继续下去的前提就是路径是真实存在的
    if os.path.exists(path):
        # 检查是否已经加载过了
        if global_value.has_key(key) is False:
            # 重新加载资源
            df_all = pd.read_excel(path)
            df_train = df_all[df_all['dev_ind'] == 1]
            df_test = df_all[df_all['dev_ind'] == 0]
            df_map = {model_name + "_" + branch:
                          {"df_all": df_all,
                           "df_train": df_train,
                           "df_test": df_test}}
            global_value.set_value(**df_map)
        else:
            df_map = global_value.get_value(key)
            df_train = df_map['df_train']
        df = ba.get_df_summary(df_train)
        # 得到df_train,将dataframe转换为用于展示前端展示的数据
        data_map = cmm.df_for_html(df)

        result = tool_model_service.load_model(model_name=model_name, model_branch=branch)
        branches = []
        v = result[0]
        for n in result:
            branches.append(n["model_branch"])

        # data_map["current_model"] = model_name
        data_map["branches"] = branches
        data_map["selected_list"] = v["selected_list"]
        data_map["target"] = v["model_target"]

        return responseto(data=data_map)
    else:
        return responseto(message="file not exist", success=False)


@app.route(base + "/create_model_name", methods=['GET'])
def create_model_name():
    model_name = request.args.get("model_name")

    if tool_model_service.load_model(model_name=model_name,model_branch="master") is None:
        if tool_model_service.create_branch():
            return responseto({"model_name":model_name,"model_branch":"master"})
        else:
            return responseto("create fail", success=False)
    return responseto("name exist", success=False)


@app.route(base + "/init_model_name", methods=['GET'])
def init_model_name():
    """
    training文件变量初始化页面,将会初始化各个模型的条目供用户选择
    :return: [model_name1,model_name2,model_name3]
    """
    result = tool_model_service.load_model(is_deleted=0)
    # only get  model_name form result

    result = list(set(map(lambda x: x["model_name"], result)))

    return responseto(result)


@app.route(base + "/get_branch_name", methods=['GET'])
def get_branch_name():
    """
    根据模型名称获得分支信息和文件路径
    :param: model_name
    :return: model_name file_path
    """
    model_name = request.args.get("modelName")
    result = tool_model_service.load_model(model_name=model_name)
    # get model_branch and file_path from result
    result = list(map(lambda x: x["model_branch"], result))

    return responseto(result)


@app.route(base + "/get_branch_info", methods=['Get'])
def get_branch_info():
    """
    根据模型名称和分支名称获得target和文件路径
    :param:model_name
    :return:
    """
    model_name = request.args.get("modelName")
    branch = request.args.get("branch")
    original_branch = request.args.get("originalBranch")
    result = tool_model_service.load_model(model_name=model_name, model_branch=branch)

    ##数据库找不到对应当前分支的数据,用户希望创建一个新分支
    if len(result) == 0:
        try:
            # tool_model_service.create_branch(model_name=model_name, model_branch=branch)
            result = tool_model_service.load_binning_record(model_name, original_branch)
            list = []
            for record in result:
                obj = [model_name, branch, record["variable_name"], record["variable_iv"], record["binning_record"],
                       record["is_selected"]]
                list.append(obj)
            record = tool_model_service.load_model(model_name=model_name, model_branch=original_branch)[0]
            result = [record]
            if tool_model_service.create_branch(model_name=model_name,
                                                model_branch=branch,
                                                model_target=record["model_target"],
                                                remove_list=record["remove_list"],
                                                selected_list=record["selected_list"]):
                tool_model_service.save_binning_record(list)
        except Exception:
            return responseto(success=False, data="添加分支失败")

    target = result[0]["model_target"]

    path = result[0]["file_path"]
    # df_map = global_value.get_value(model_name+"_"+branch)
    selected_file = None

    # if path is not None and \
    #         global_value.has_key(model_name+"_"+branch) is not True\
    #         and os.path.exists(path):
    #     df_all = pd.read_excel(path)
    #     df_train = df_all[df_all['dev_ind'] == 1]
    #     df_test = df_all[df_all['dev_ind'] == 0]
    #     df_map = {model_name + "_" + branch:
    #                   {"df_all": df_all,
    #                    "df_train": df_train,
    #                    "df_test": df_test}}
    #     global_value.set_value(**df_map)
    if path is not None:
        selected_file = path.replace(app.config["ROOT_PATH"] + "/", "")


    files = common_util.listFile(app.config["ROOT_PATH"], absolute=False)

    data = {"target": target, "selected_file": selected_file, "files": files}

    return responseto(data)


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
        obj = [model_name, branch, record["variable_name"], record["variable_iv"], record["binning_record"],
               record["is_selected"]]
        list.append(obj)

    if tool_model_service.copy_branch(model_name, branch, original_branch):
        tool_model_service.save_binning_record(list)
        return responseto(data=True)
    return responseto(data=False)
