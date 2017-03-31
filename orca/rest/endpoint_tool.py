# -*- coding: utf-8 -*-
from werkzeug.utils import secure_filename

from rest.app_base import *
from util import Initial_Binning as ib
import pandas as pd
from util import Adjust_Binning as ab
from util import common as cmm
import json
from collections import OrderedDict

import numpy as np
import collections

base = '/tool'
base_path = "./util/"


def file_init():
    train_file = base_path + "df_train.xlsx"
    train = pd.read_excel(train_file)

    test_file = base_path + "df_test.xlsx"
    test = pd.read_excel(test_file)
    return [train, test]


# df_list = file_init()
# df_train = file_init()
# df_test = file_init()
df_train = pd.read_excel("/Users/lifeng/Desktop/df_train.xlsx")
# df_train = None
# df_test = pd.read_excel("/Users/lifeng/Desktop/df_test.xlsx")
df_test = None


@app.route(base + "/init")
def init():
    # min = request.form.get("min")
    min_val = 0
    df = df_train
    out = get_init(df)

    out = get_boundary(out, min_val)
    # for
    # first_bin = val[0]
    # if first_bin["category_t"] == False:
    #     val[0]["min"] = min_val
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

    min_val = 0

    result = None
    type_bool = False
    df = None
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

        columns = ['bin_num', 'min', 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe',
                   'category_t']
    else:
        type_bool = True
        selected_list = []
        temp = []
        for s in boundary.split("&"):
            temp.extend(map(cmm.transfer, s.split("|")))

        selected_list = [temp]
        for s in all_boundary.split("&"):
            selected_list.append(map(cmm.transfer, s.split("|")))

        columns = ['bin_num', var_name, 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe',
                   'category_t']

    result = ab.adjust(df_train, type_bool, var_name, selected_list)  # 获得合并的结果
    df = pd.DataFrame(result[0],
                      columns=columns)

    data = {var_name: []}
    row_index = 0

    for index, row in df.iterrows():  # 获取每行的index、row
        sub_data = collections.OrderedDict()
        for col_name in df.columns:
            if isinstance(row[col_name],list):
                sub_data[col_name] = "|".join(str(i.encode('utf-8')) for i in row[col_name])
            else:
                sub_data[col_name] = str(row[col_name])
            if col_name == 'max':
                # 在max后额外增加两列
                sub_data['min_bound'] = row["min"]
                sub_data['max_bound'] = row["max"]
                if row_index == len(df) - 1:
                    if [row["bin_num"] - df.iloc[[row_index - 1]]["bin_num"]] > 1:
                        sub_data["bin_num"] = row["bin_num"] - 1
                row_index += 1
        data[var_name].append(sub_data)

        data = get_boundary(data, min)
    return responseto(data=data)


@app.route(base + "/divide", methods=['POST'])
def divide():
    data = request.form.get('data')

    map = json.loads(data,object_pairs_hook=OrderedDict)
    name = map["name"]
    target = "bad_7mon_60"
    df = pd.DataFrame(df_train, columns={target, name})
    if map["selected"]["category_t"] == 'False':
        min = map["selected"]["min"]
        max = map["selected"]["max"]
        for index, row in df.iterrows():
            if float(min) <= float(row[name]) < float(max):
                pass
            else:
                df.drop(index, inplace=True)
    else:
        val = map["selected"][name].split("|")
        for index, row in df.iterrows():
            if row[name] in val:
                pass
            else:
                df.drop(index, inplace=True)
    #得到分裂的结果
    out = get_init(df)
    print json.dumps(out[name])
    list = map["table"]
    #删除要被分裂的项
    del list[map["selectedIndex"]]
    #被分裂的项的下标
    index = map["selectedIndex"]
    #将分裂的结果加入原有的列表中
    for v in out[name]:
        obj = collections.OrderedDict()
        for key in v.keys():
            obj[key] = v[key]
        list.insert(index, obj)
        index += 1

    #重新划定index
    for index, v in enumerate(list):
        v["bin_num"] = index
        v["total"] = (round(v['total']/map["all"],4) * 100 )+ '%'

    list = get_boundary((name,list),0)[1]
    return responseto(data={name:list})


@app.route(base + "/apply", methods=['POST'])
def apply():
    """将train数据得到的woe与test数据进行匹配"""
    data = request.form.get('data')
    dict = json.loads(data)

    test = df_test.drop('bad_7mon_60', 1)
    vars = df_test.columns
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
                        bin_min = float(obj["min"])
                        bin_max = float(obj["max"])
                        bin_val = float(row[column])
                        if bin_max == bin_min and bin_val == bin_min:
                            test.loc[index, [column + "_woe"]] = obj["woe"]
                            break
                        elif bin_min <= bin_val < bin_max:
                            test.loc[index, [column + "_woe"]] = obj["woe"]
                            break
                        elif obj["max"] == 'nan' and str(row[column]) == 'nan':
                            test.loc[index, [column + "_woe"]] = obj["woe"]
                            break
                    else:
                        # categorical,直接进行匹配
                        if row[column] in obj[column]:
                            test.loc[index, [column + "_woe"]] = obj["woe"]
                            break
    test.to_excel("df_iv.xlsx", ",", header=True, index=False)
    return responseto(data=test)


@app.route(base + "/upload", methods=['OPTIONS', 'POST'])
def upload():
    """工具依赖的源文件修改"""
    # 在跨域的情况下,前端会发送OPTIONS请求进行试探,然后再发送POST请求
    if request.method == 'POST':
        global df_train
        global df_test
        files = request.files.getlist("file[]")
        for file in files:
            filename = secure_filename(file.filename)
            print filename
            if filename == 'df_test.xlsx':
                df_test = pd.read_excel(file)
            elif filename == 'df_train.xlsx':
                df_train = pd.read_excel(file)
    return responseto(data="success")


def get_init(df=df_train):
    data_map = ib.cal(df)
    keys = data_map.keys()
    out = collections.OrderedDict()
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
                    if col_name == 'max':
                        row_data['min_bound'] = row["min"]
                        row_data['max_bound'] = row["max"]

            subList.append(row_data)
            row_data = collections.OrderedDict()
        out[var_name] = subList
    return out


def get_boundary(out, min_val):
    if isinstance(out,dict):
        data = out.items()
    else:
        data = [out]
    for val in data:
        index = 0
        last_bin = None
        for bin_row in val[1]:
            if bin_row["category_t"] == "False":
                if index == 0:
                    # if float(bin_row["min"]) >= min_val:
                    last_bin = bin_row
                    bin_row["min_bound"] = min_val
                else:
                    if bin_row["min"] != 'nan':
                        last_bin["max_bound"] = bin_row["min_bound"]
                        last_bin = bin_row
                    else:
                        last_bin["max_bound"] = 'inf'
                index = index + 1
            else:
                break

    return out
