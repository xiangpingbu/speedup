# coding=utf-8
from rest.app_base import *
from service import db_service as vs
import requests
import json
from collections import OrderedDict
from datetime import datetime
from common.constant import const
from service.db import tool_model_service




base = "/tool/db"
es_host = "http://10.10.10.107:9200/"


@app.route(base + "/<string:name>")
def es_req(key):
    url = app.config["es_host"] + key + "/_search?pretty"
    response = requests.get(url)
    return responseto(data=json.loads(response.text))

'''
@pre-init步骤提交该分支的信息
'''
@app.route(base + "/branch/commit-branch", methods=['POST'])
def commit_branch():
    model_name = request.form.get("model_name")
    branch = request.form.get("branch")
    selected_list = request.form.get("selected_list")
    target = request.form.get("target")


    return responseto(data=vs.update_branch(model_name, branch, target, selected_list=selected_list))


'''
@pre-init切换分支
'''
@app.route(base + "/branch/checkout", methods=['GET'])
def checkout():
    model_name = request.values.get("model_name")
    branch = request.values.get("branch")
    result = vs.load_branch(model_name, branch)

    return responseto(data=result[0])


@app.route(base + "/save", methods=['Post'])
def save():
    model_name = request.values.get("model_name")
    branch = request.values.get("branch")
    data = request.values.get("data")
    dict = json.loads(data)

    vs.del_binnbing_record(model_name, branch)

    list = []
    for key, val in dict.items():
        now = datetime.now()
        obj = [model_name, branch, key, val["iv"], json.dumps(val["var_table"],ensure_ascii=False),val["is_selected"]]
        list.append(obj)
    if vs.save_binning_record(list) is not True:
        return responseto(success=False)
    return responseto()


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
    return responseto(data = data)


def sort_iv(data):
    out_sorted_iv = OrderedDict(sorted(data.items(), key=lambda v: v[1]["iv"], reverse=True))
    return out_sorted_iv