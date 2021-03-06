# coding=utf-8
from collections import OrderedDict

import requests
import pandas as pd
from flask import request

from util import restful_tools as rest
from rest.app_base import app
from common.constant import const
from service.db import tool_model_service
from beans.tool_model import *
from common import  global_value




base = "/tool/db"
es_host = "http://10.10.10.107:9200/"


@app.route(base + "/<string:name>")
def es_req(key):
    url = app.config["es_host"] + key + "/_search?pretty"
    response = requests.get(url)
    return rest.responseto(data=json.loads(response.text))

'''
@pre-init步骤提交该分支的信息
'''
@app.route(base + "/branch/commit-branch", methods=['POST'])
def commit_branch():
    model_name = request.form.get("model_name")
    branch = request.form.get("branch")
    selected_list = request.form.get("selected_list")
    target = request.form.get("target")
    file_path = request.form.get("file_path")

    root_path = app.config["ROOT_PATH"]
    path = root_path + "/" + file_path

    key = model_name+"_"+branch
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

    return rest.responseto(data=tool_model_service.update_branch(model_name, branch, target, selected_list=selected_list, file_path = file_path))


'''
@pre-init切换分支
'''
@app.route(base + "/branch/checkout", methods=['GET'])
def checkout():
    model_name = request.values.get("model_name")
    branch = request.values.get("branch")
    result = tool_model_service.load_model(model_name=model_name, model_branch = branch)

    return rest.responseto(data=result[0])


@app.route(base + "/save", methods=['Post'])
def save():
    model_name = request.values.get("model_name")
    branch = request.values.get("branch")
    data = request.values.get("data")
    dict = json.loads(data)

    tool_model_service.del_binning_record(model_name, branch)

    list = []
    for key, val in dict.items():
        obj = ModelContent(model_name=model_name,
                    model_branch=branch,
                    variable_name=key,
                    variable_iv=val["iv"],
                    binning_record=json.dumps(val["var_table"],ensure_ascii=False),
                    is_selected=val["is_selected"])
        list.append(obj)
    if tool_model_service.save_binning_record(list) is not True:
        return rest.responseto(success=False)
    return rest.responseto()


@app.route(base + "/load_all", methods=['Post'])
def load_all():
    model_name = request.values.get("model_name")
    branch = request.values.get("branch")

    result = tool_model_service.load_binning_record(model_name, branch)

    data = {}
    if result is not None:
        for row in result:
            data[row["variable_name"]] = {"iv": row["variable_iv"],
                                          "var_table": json.loads(row["binning_record"]),
                                          "is_selected":row["is_selected"]==const.SELECTED}
    return rest.responseto(data = data)


def sort_iv(data):
    out_sorted_iv = OrderedDict(sorted(data.items(), key=lambda v: v[1]["iv"], reverse=True))
    return out_sorted_iv