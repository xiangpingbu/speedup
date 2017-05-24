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

