# coding=utf-8
from rest.app_base import *
import requests
import json

base="/es"
es_host="http://10.10.10.107:9200/"

@app.route(base + "/resource/<string:key>")
def get_res(key):
    url = es_host + key+"/_search?pretty"
    d = json.dumps({"size": 5000})
    # response = requests.get(url)
    response = requests.post(url, data=d)
    # print(response)
    return responseto(data=json.loads(response.text))


# # 根据name获取资源中的某一个
# @app.route('/language/<string:name>')
# def getOne(name):
#     result = [data for data in datas if data['name'] == name]
#     if len(result) == 0:
#         return statusResponse(R404_NOTFOUND)
#     return fullResponse(R200_OK, result[0])


# print requests.get("http://www.baidu.com").text
