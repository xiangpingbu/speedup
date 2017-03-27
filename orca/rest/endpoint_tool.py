# -*- coding: utf-8 -*-
from rest.app_base import *
from util import Initial_Binning as ib
import pandas as pd
from util import Adjust_Binning as ab
from util import common as cmm
import json

import numpy as np
import collections

base = '/tool'
base_path = "./util/"
# df_test = base_path + "df_test.xlsx"
train_file = base_path + "df_train.xlsx"
df_train = pd.read_excel(train_file)

test_file = base_path + "df_test.xlsx"
df_test = pd.read_excel(test_file)


merge_record = {}


@app.route(base + "/init")
def init(df=df_train):
    out = get_init(df)
    return responseto(data=out)


@app.route(base + "/merge", methods=['POST'])
def merge():
    """归并操作"""
    # 要执行合并的variable
    var_name = request.form.get('varName').encode('utf-8')
    # 变量的类型
    type = request.form.get('type').encode('utf-8')
    # 选定的范围
    boundary = request.form.get('boundary').encode('utf-8')  # 每个bin_num的max的大小,都以逗号隔开
    # 总的范围
    all_boundary = request.form.get('allBoundary').encode('utf-8')  # 每个bin_num的max的大小,都以逗号隔开

    result = None
    type_bool = False
    if type == 'False':
        # 将字符转换为list
        boundary_list = map(eval, boundary.split("&"))
        all_boundary_list = []
        # 将字符转换为list,nan替换为np.nan
        for a in all_boundary.split("&"):
            if a != 'nan':
                a = float(a)
            else:
                a = np.nan
            all_boundary_list.append(a)
        boundary_list = list(set(all_boundary_list).difference(set(boundary_list)))
        boundary_list.append(np.nan)
        selected_list = boundary_list
    else:
        type_bool = True
        temp = []
        for s in boundary.split("&"):
            temp.extend(map(cmm.transfer, s.split('|')))

        all_temp = []

        for s in all_boundary.split("&"):
            all_temp.extend(map(cmm.transfer, s.split("|")))
        extra_list = list(set(all_temp).difference(set(temp)))
        selected_list = []
        # 可能合并了所有的选项,就不必加入这个list
        if len(selected_list) != 0:
            selected_list.append(extra_list)
        # boundary_list = map(cmm.replace,boundary_list)
        selected_list.append(temp)

    result = ab.adjust(df_train, type_bool, var_name, selected_list)  # 获得合并的结果
    data = {var_name: []}
    for index, row in result[0].iterrows():  # 获取每行的index、row
        sub_data = collections.OrderedDict()
        for col_name in result[0].columns:
            if col_name == 'IV' or col_name == 'odds' or col_name == 'ks':
                continue
            sub_data[col_name] = str(row[col_name])
        data[var_name].append(sub_data)
    return responseto(data=data)


@app.route(base + "/divide", methods=['POST'])
def divide():
    data = request.form.get('data')
    map = json.loads(data)
    min = map["selected"]["min"]
    max = map["selected"]["max"]
    name = map["name"]
    target = "bad_7mon_60"
    df = pd.DataFrame(df_train, columns={target, name})
    for index, row in df.iterrows():
        if float(min) < float(row[name]) <= float(max):
            pass
        else:
            df.drop(index,inplace=True)
    out = get_init(df)
    print json.dumps(out[name])
    list = map["table"]
    del list[map["selectedIndex"]]

    index = map["selectedIndex"]
    for v in out[name]:
        obj={}
        for key in v.keys():
            obj[key] = v[key]
        list.insert(index,obj)
        index = index+1

        for index,v in enumerate(list):
            v["bin_num"] = index
    return responseto(data={name:list})


@app.route(base + "/apply", methods=['POST'])
def apply():
    """将train数据得到的woe与test数据进行匹配"""
    data = request.form.get('data')
    dict = json.loads(data)

    vars = df_test.columns
    test = df_test.drop('bad_7mon_60', 1)
    test_copy = test.copy()
    # 初始化列
    for v in vars:
        test[v + "_woe"] = ""

    for index, row in test_copy.iterrows():
        for column in test_copy.columns:
            bins = dict[column]
            if bins is not None:
                for obj in bins:
                    # 根据category_t的布尔值区分类别,如果为false为numerical
                    if obj["category_t"] == "False":
                        # 比对区间,获得woe的值
                        if float(obj["min"]) < row[column] <= float(obj["max"]):
                            test.loc[index, [column + "_woe"]] = obj["woe"]
                            break
                        if obj["max"] == 'nan':
                            test.loc[index, [column + "_woe"]] = obj["woe"]
                    else:
                        # categorical,直接进行匹配
                        if row[column] == obj[column]:
                            test.loc[index, [column + "_woe"]] = obj["woe"]
                            break

    test.to_excel("df_iv.xlsx", ",", header=True, index=False)
    return responseto(data=test)


def get_init(df=df_train):
    data_map = ib.cal(df)
    keys = data_map.keys()
    out = {}
    for k in keys:
        row_data = collections.OrderedDict()
        c = data_map[k]
        subList = []
        var_name = c[0]
        # var_type = c[1]
        woe_map = c[2]
        boundary = c[3]
        for index, row in woe_map.iterrows():  # 获取每行的index、row
            for col_name in woe_map.columns:
                if isinstance(row[col_name], np.ndarray):
                    row_data[col_name] = "|".join(str(i.encode('utf-8')) for i in row[col_name].tolist())
                else:
                    row_data[col_name] = str(row[col_name])
            subList.append(row_data)
            row_data = collections.OrderedDict()
        out[var_name] = subList
    return out
