# coding=utf-8
"""
通过binning record获得column config
"""
import json
from beans import Pmml
import requests
from xml.dom import minidom


def sort_variable(variables, result):
    v = {}
    for index, name in enumerate(variables):
        v[name.decode('utf-8')] = index

    new_result = [{}] * (len(v))
    for variable in result:
        i = v[variable['variable_name'].decode('utf-8')]
        new_result[i] = variable

    return new_result


def column_config(list, params, result):
    """
    将配置完成的variable数据转化一定格式的json数据
    这些数据会用于生成pmml

    Parameters:
        data: variable的的行列信息
    """

    # 从数据库中得到binning_record,并根据list中variable的顺序排序
    p_value_list = []
    data = []
    for index, variable in enumerate(result):
        variable_name = variable["variable_name"]
        p_value_list.append({variable_name: params[index]})
        records = variable["binning_record"]
        first_row = records[0]
        # 如果type为true,那么为Numrical
        type = first_row["type"] == 'Numerical'
        # 如果bin_num为0,那么这一行woe值为missing值
        if first_row["bin_num"] == '0' and type:
            missing_woe = first_row["woe"]
            del records[0]
        else:
            missing_woe = 0
        columnBinning = {"binCountNeg": [],
                         "binCountPos": [],
                         "binWeightedPos": [],
                         "binWeightedWoe": [],
                         "binAvgScore": [],
                         "binWeightedNeg": [],
                         "binPosRate": []}
        pmml = Pmml.Pmml()
        pmml.columnFlag = None
        pmml.finalSelect = True
        pmml.columnName = variable_name
        pmml.columnBinning = columnBinning

        if type:
            pmml.columnType = "N"
            columnBinning["binBoundary"] = ["-Infinity"]
            columnBinning["binCategory"] = None
            # 0指代invalid的值
            columnBinning["binCountWoe"] = [0]
        else:
            pmml.columnType = "C"
            columnBinning["binCategory"] = []
            columnBinning["binBoundary"] = None
            columnBinning["binCountWoe"] = []

        index = 0
        categorical_nan_woe = 0
        categorical_list = []
        categorical_woe_list = []
        for val in records:
            columnBinning["binCountNeg"].append(1)
            columnBinning["binCountPos"].append(2)

            if type:
                columnBinning["binBoundary"].append(float(val["min_boundary"]))
                columnBinning["binCountWoe"].append(float(val["woe"]))
                if index == len(records) - 1:
                    columnBinning["binCountWoe"].append(float(missing_woe))
            else:
                # categorical的woe值
                # for cate in records:
                v_list = val[variable_name.decode('utf-8')].split("|")
                for v in v_list:
                    if v != 'nan':
                        columnBinning["binCategory"].append(v)
                        columnBinning["binCountWoe"].append(val["woe"])
                    else:
                        categorical_nan_woe = val['woe']
            index += 1

        if type:
            columnBinning["length"] = len(columnBinning['binBoundary'])
        else:
            columnBinning["length"] = len(columnBinning["binCategory"])
            columnBinning["binCategory"].append("missing")
            columnBinning["binCountWoe"].append(categorical_nan_woe)
            columnBinning["binCategory"].append('invalid')
            columnBinning["binCountWoe"].append(0)

        data.append(pmml.__dict__)

    column_config = json.dumps(data, ensure_ascii=False)
    post_data = {"column_config": json.dumps(data, ensure_ascii=False),
                 "params": ','.join(params)}
    # resp = requests.post("http://10.10.10.100:8082/maas/rest/pmml/generate", data=post_data)
    resp = requests.post("http://localhost:8082/maas/rest/pmml/generate", data=post_data)
    pmml_xml = resp.text
    doc = minidom.parseString(pmml_xml.encode("utf-8"))
    xml_str = doc.toprettyxml(indent="  ", newl="\n", encoding='utf-8')
    print xml_str


# binning recordd的内容
# file_path = '/Users/lifeng/Library/Containers/com.tencent.xinWeChat/Data/Library/Application ' \
#             'Support/com.tencent.xinWeChat/2.0b4.0.9/e95dc620ef049ee0f78c04b5aaa20b6e/Message/MessageTemp' \
#             '/bf41c67aaf05640f4129133babc42bf9/File/procident_fund.json '

file_path = '/Users/lifeng/Library/Containers/com.tencent.xinWeChat/Data/Library/Application Support/com.tencent.xinWeChat/2.0b4.0.9/e95dc620ef049ee0f78c04b5aaa20b6e/Message/MessageTemp/bf41c67aaf05640f4129133babc42bf9/File/social_insurance(2).json'
# params = ["1", "1", "1", "1", "1", "1", "1"]

fp = open(file_path)
fund_json = json.load(fp, encoding='utf-8')

key_list = map(lambda x: x, fund_json.keys())
print ','.join(key_list)

binning_list = list()
params = list()
for key in fund_json.keys():
    binning_list.append({"variable_name": key, "binning_record": fund_json[key]})

    obj = fund_json[key][0]
    params.append(str(fund_json[key][0]['coefficient']))


result = sort_variable(key_list, binning_list)

column_config(key_list, params, binning_list)
