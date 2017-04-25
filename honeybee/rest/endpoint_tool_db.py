# coding=utf-8
from rest.app_base import *
import requests
import json

base="/tool/db"
es_host="http://10.10.10.107:9200/"

@app.route(base + "/<string:name>")
def get_res(key):
    url = es_host + key+"/_search?pretty"
    response = requests.get(url)
    return responseto(data=json.loads(response.text))

