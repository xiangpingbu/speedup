# coding=utf-8
from rest.app_base import *
import requests
import json
import tablib
from service.db import tool_model_service



base="/tool"
es_host="http://10.10.10.107:9200/"


@app.route(base + "/export_selected_variable", methods=['POST'])
def export_selected_variable():
    """导出被选择的变量"""
    data = request.form.get("data")
    dataDict = json.loads(data)
    branch = dataDict["branch"]
    type = dataDict["type"]
    model_name = dataDict["model_name"]
    if type == None:
        type = "xlsx"
    result = tool_model_service.load_binning_record(model_name, branch)
    # result = filter(lambda x: x["is_selected"] >0,result)
    new_result = []
    for record in result:
        new_record = []
        new_record.append(record["variable_name"])
        new_record.append(record["variable_iv"])
        new_record.append(record["is_selected"])
        new_result.append(new_record)
    headers = ('variable_name', 'variable_iv', 'is_selected')
    data = tablib.Dataset(*new_result, headers=headers)

    # 实例化一个Workbook()对象(即excel文件)
    resp = rest.make_response(data.xlsx)
    return rest.responseto(resp, "selected_variable." + type)

@app.route(base+"/export_score_card",methods=['POST'])
def export_score_card():
    data = request.form.get("data").decode("utf-8")
    data_dict = json.loads(data)
    branch = data_dict["branch"]
    model_name = data_dict["model_name"]

    #key-value形式
    coefficients = data_dict["coefficient"]

    result = tool_model_service.load_binning_record(model_name, branch)
    inner_head = ["","bin_num","",
                  "","bads",
                  "goods","total",
                  "total_perc","bad_rate","woe"]
    new_result = []
    for record in result:
        variable_name = record["variable_name"]
        coe = coefficients[variable_name]

        head = inner_head[:]
        new_result.append(head)

        head[0] = variable_name
        binning_records = json.loads(record["binning_record"])
        for r in binning_records:
            if r["type"] == "Numerical":
                head[2] = "min_boundary"
                head[3] = "max_boundary"
                list = ["",r["bin_num"],r["min_boundary"],r["max_boundary"],r["bads"],r["goods"],
                        r["total"],r["total_perc"],r["bad_rate"],r["woe"],coe]
            else:
                head[2] = variable_name
                head[3] = ""
                list = ["",r["bin_num"],"","",r["bads"],r["goods"],
                        r["total"],r["total_perc"],r["bad_rate"],r["woe"],coe]
            new_result.append(list)
        new_result.append([])
    data = tablib.Dataset(*new_result,headers=[""]*11)

    open('/Users/lifeng/Desktop/111/xxx.xlsx', 'wb').write(data.xlsx)


