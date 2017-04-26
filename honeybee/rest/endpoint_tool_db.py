# coding=utf-8
from rest.app_base import *
from service import variable_service as vs
import requests
import json

base="/tool/db"
es_host="http://10.10.10.107:9200/"

@app.route(base + "/<string:name>")
def es_req(key):
    url = es_host + key+"/_search?pretty"
    response = requests.get(url)
    return responseto(data=json.loads(response.text))

@app.route(base + "/branch",methods=['POST'])
def new_branch():
    model_name = request.form.get("model_name")
    branch = request.form.get("branch")

    return responseto(data=vs.create_branch(model_name,branch))

'''
@pre-init步骤提交该分支的信息
'''
@app.route(base + "/branch/commit-branch",methods=['POST'])
def commit_branch():
    model_name = request.form.get("model_name")
    branch = request.form.get("branch")
    remove_list = request.form.get("remove_list")
    target = request.form.get("target")

    return responseto(data=vs.update_branch(model_name,branch,target,remove_list))

'''
@pre-init切换分支
'''
@app.route(base + "/branch/checkout", methods=['GET'])
def checkout():
    model_name = request.values.get("model_name")
    branch = request.values.get("branch")
    result = vs.load_branch(model_name,branch)

    return responseto(data=result[0])

@app.route(base+"/save",methods=['Post'])
def save():
    data = request.values.get("data")
    dict = json.loads(data)

    for key,val in dict.items:

