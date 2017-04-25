# -*- coding: utf-8 -*-
from werkzeug.utils import secure_filename

from beans.Pmml import *
from rest.app_base import *
from util import Initial_Binning as ib
import pandas as pd
from util import Adjust_Binning as ab
from util import common as cmm
import json
from collections import OrderedDict
import numpy as np
import collections
from util import A99_Functions as a99
from io import BytesIO
from flask import send_file
from service import variable_service as vs

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
model_name = "df_train"
df_train = pd.read_excel("/Users/lifeng/Downloads/py/df_train.xlsx")
# df_train = None
# df_test = pd.read_excel("/Users/lifeng/Desktop/df_test.xlsx")
df_test = None


@app.route(base + "/init", methods=['POST'])
def init():
    name = request.form.get("model_name")
    branch = request.form.get("branch")

    result = vs.load_branch(name,branch)

    # if var_service.if_branch_exist(model, branch):
    #     var_service.update_branch(model, branch, remove_list, selected_list)
    # else:
    #     var_service.create_branch(model, branch, target, remove_list, selected_list)

    remove_list_json = json.loads(json.loads(result[0]["remove_list"]))
    remove_list = []
    for o in remove_list_json :
        remove_list.append(o)

    # remove_list.append(target)

    # invalid = invalid.split(",")
    # min = request.form.get("min")
    min_val = 0
    df = df_train
    out = get_init(df, target=result[0]["model_target"], invalid=remove_list)

    out = get_boundary(init_result, min_val)
    # for
    # first_bin = val[0]
    # if first_bin["category_t"] == False:
    #     val[0]["min"] = min_val
    return responseto(data=out)


@app.route(base + "/merge", methods=['POST'])
def merge():
    """归并操作"""
    # 要执行合并的variable
    var_name = request.form.get('varName')
    # 变量的类型
    type = request.form.get('type').encode('utf-8')
    # 选定的范围
    boundary = request.form.get('boundary').encode('utf-8')  # 每个bin_num的max的大小,都以逗号隔开
    # 总的范围
    all_boundary = request.form.get('allBoundary').encode('utf-8')  # 每个bin_num的max的大小,都以逗号隔开
    # 获得target
    # target = request.form.get('allBoundary').encode('utf-8');
    target = request.form.get('target')
    if target is None:
        target = 'bad_4w'
    excepted_column = {var_name}

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

        columns = ['bin_num', 'min', 'max', 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe',
                   'category_t']
    else:
        type_bool = True
        temp = []
        for s in boundary.split("&"):
            temp.extend(map(cmm.transfer, s.split("|")))

        selected_list = [temp]
        for s in all_boundary.split("&"):
            selected_list.append(map(cmm.transfer, s.split("|")))

        columns = ['bin_num', var_name, 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe',
                   'category_t']

    result = ab.adjust(df_train, type_bool, var_name, selected_list, target=target,
                       expected_column=excepted_column)  # 获得合并的结果
    df = pd.DataFrame(result[0],
                      columns=columns)

    data = get_merged(var_name, df, min_val)
    return responseto(data=data)


@app.route(base + "/divide", methods=['POST'])
def divide():
    """
    分裂操作
    先将从data中得到的范围,从excel中筛选相应的数据
    筛选完成后,调用init方法对数据进行初始化,得到一定数据的范围区间
    将该范围区间与原来的区间合并.
    调用adjust方法获得的结果即为分裂后的结果

    :return:
    """

    min_val = 0
    data = request.form.get('data')
    # 解析json
    data_map = json.loads(data, object_pairs_hook=OrderedDict)
    name = data_map["name"]
    target = "bad_4w"
    # 将excel转化为dataframe,只读取target和name两列
    df = pd.DataFrame(df_train, columns={target, name})

    bound_list = None
    if data_map["selected"]["type"] == 'Numerical':
        # 根据min和max的边界去筛选数据
        min = data_map["selected"]["min_boundary"]
        max = data_map["selected"]["max_boundary"]
        df = df[(df[name].astype(float) >= float(min)) & (df[name].astype(float) < float(max))]

        #for index, row in df.iterrows():
        #    if float(min) <= float(row[name]) < float(
        # max):
        #        pass
        #    else:
        #        df.drop(index, inplace=True)

        out = get_init(df, target=target, invalid=[])
        bound_list = get_divide_min_bound(out)

        list = data_map["table"]
        # 删除要被分裂的项
        del list[data_map["selectedIndex"]]

        for v in list:
            bound_list.append(float(v["min_boundary"]))
        #bound_list.append(np.nan)

        result = ab.adjust(df_train, data_map["selected"]["type"] == 'Categorical', name, bound_list
                           , target=target, expected_column={name})
        columns = ['bin_num', 'min', 'max', 'min_boundary', 'max_boundary', 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe',
                   'type']
        df = pd.DataFrame(result,
                          columns=columns)
        data = generate_response(name, df)
        #data = get_merged(name, df, min_val)

        return responseto(data=data)

    else:
        val = data_map["selected"][name].split("|")
        for index, row in df.iterrows():
            if row[name] in val:
                pass
            else:
                df.drop(index, inplace=True)

        list = data_map["table"]
        # 删除要被分裂的项
        del list[data_map["selectedIndex"]]

        out = get_init(df, target=target, invalid=[])
        bound_list = get_divide_caterotical_bound(out, name)
        # 被分裂的项的下标
        index = data_map["selectedIndex"]
        # 将分裂的结果加入原有的列表中
        for v in list:
            bound_list.append(map(cmm.transfer, v[name].split("|")))
        result = ab.adjust(df_train, data_map["selected"]["type"] == 'Categorical', name, bound_list
                           ,target=target, expected_column={name})
        columns = ['bin_num', name, 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe',
                   'type']
        df = pd.DataFrame(result,
                          columns=columns)

        data = generate_response(name, df)
        #data = get_merged(name, df, min_val)
        return responseto(data = data)


@app.route(base + "/apply", methods=['POST'])
def apply():
    """将train数据得到的woe与test数据进行匹配"""
    data = request.form.get('data')
    target = request.form.get('target')
    if target is None:
        target = "bad_4w"
    dict = json.loads(data)
    df_test.append(df_train)
    test = df_test.drop(target, 1)
    # vars = df_test.columns
    keys = dict.keys()
    test_copy = test.copy()
    # 初始化列
    for v in keys:
        test[v + "_woe"] = ""
    for index, row in test_copy.iterrows():
        for column in dict:
            bins = dict[column]
            if bins is not None:
                for obj in bins:
                    # 根据category_t的布尔值区分类别,如果为false为numerical
                    if obj["category_t"] == "False":
                        # 比对区间,获得woe的值
                        bin_min = float(obj["min_bound"])
                        bin_max = float(obj["max_bound"])
                        bin_val = float(row[column])
                        if bin_max == bin_min and bin_val == bin_min:
                            test.loc[index, [column + "_woe"]] = obj["woe"]
                            break
                        elif bin_min <= bin_val < bin_max:
                            test.loc[index, [column + "_woe"]] = obj["woe"]
                            break
                        elif obj["max_bound"] == 'nan' and str(row[column]) == 'nan':
                            test.loc[index, [column + "_woe"]] = obj["woe"]
                            break
                        elif obj['min_bound'] == 'nan' and bin_val < bin_max:
                            test.loc[index, [column + "_woe"]] = obj["woe"]
                            break
                    else:
                        # categorical,直接进行匹配
                        if str(row[column]) in obj[column]:
                            test.loc[index, [column + "_woe"]] = obj["woe"]
                            break
    # test.to_excel("df_iv.xlsx", ",", header=True, index=False)
    output = BytesIO()
    writer = pd.ExcelWriter(output, engine='xlsxwriter')

    test.to_excel(writer, startrow=0, merge_cells=False, sheet_name="Sheet_1")
    workbook = writer.book
    worksheet = writer.sheets["Sheet_1"]
    format = workbook.add_format()
    format.set_bg_color('#eeeeee')
    worksheet.set_column(0, 9, 28)
    writer.close()

    output.seek(0)
    response = make_response(send_file(output, attachment_filename="df_iv.xlsx", as_attachment=True))
    return responseFile(response)


@app.route(base + "/upload", methods=['OPTIONS', 'POST'])
def upload():
    """工具依赖的源文件修改"""
    # 在跨域的情况下,前端会发送OPTIONS请求进行试探,然后再发送POST请求
    if request.method == 'POST':
        global df_train
        global df_test
        global model_name
        files = request.files.getlist("file[]")
        for file in files:
            filename = secure_filename(file.filename)
            model_name = filename[0:filename.index(".")]
            print filename
            if filename == 'df_test.xlsx':
                df_test = pd.read_excel(file, encoding="utf-8")
            elif filename == 'df_train.xlsx':
                df_train = pd.read_excel(file, encoding="utf-8")
                # df_train['bad_7mon_60'] = df_train['bad_4w']
    return responseto(data="success")


@app.route(base + "/parse", methods=['GET'])
def parse():
    df = a99.GetDFSummary(df_train)
    data_map = cmm.df_for_html(df)
    result = vs.load_model(model_name)
    if len(result) < 1:
        vs.create_branch(model_name, "master", None, None)
        result["model_branch"] = ["master"]

    branches = []

    # 只取master
    v = result[0]
    if v["remove_list"] is not None:
        remove_list = v["remove_list"]


    for n in result:
        branches.append(n["model_branch"])

    data_map["current_model"] = model_name
    data_map["branches"] = branches
    data_map["remove_list"] = remove_list
    return responseto(data=data_map)


@app.route(base + "/column-config", methods=['POST'])
def column_config():
    """
    将配置完成的variable数据转化一定格式的json数据
    这些数据会用于生成pmml

    Parameters:
        data: variable的的行列信息
    """
    data = request.form.get('data')
    dataMap = json.loads(data)
    result = []
    columnNum = 1
    for key, list in dataMap.items():
        pmml = Pmml()
        pmml.columnNum = columnNum

        first_bin = list[0]

        columnBinning = {"binCountNeg": [],
                         "binCountPos": [],
                         "binWeightedPos": [],
                         "binWeightedWoe": [],
                         "binAvgScore": [],
                         "binWeightedNeg": [],
                         "binPosRate": []}
        pmml.columnFlag = None
        pmml.finalSelect = True
        pmml.columnName = key
        pmml.columnBinning = columnBinning

        type = first_bin["category_t"] == "False"

        if type:
            pmml.columnType = "N"
            columnBinning["binBoundary"] = ["-Infinity"]
            columnBinning["binCategory"] = None
            columnBinning["binCountWoe"] = [0]
        else:
            pmml.columnType = "C"
            columnBinning["binCategory"] = ["missing", "invalid"]
            columnBinning["binBoundary"] = None
            columnBinning["binCountWoe"] = [0, 0]

        index = 0
        for val in list:
            columnBinning["binCountNeg"].append(1)
            columnBinning["binCountPos"].append(2)

            if type:
                if index != len(list) - 1:
                    columnBinning["binBoundary"].append(float(val["min_bound"]))

                columnBinning["binCountWoe"].append(float(val["woe"]))

            else:
                # categorical的woe值
                for cate in val[key]:
                    columnBinning["binCategory"].insert(0, cate)
                    columnBinning["binCountWoe"].insert(0, float(val["woe"]))
            index += 1

        if type:
            columnBinning["length"] = len(columnBinning['binBoundary'])
        else:
            columnBinning["length"] = len(columnBinning["binCategory"])
        result.append(pmml.__dict__)
        columnNum += 1
    print json.dumps(result)
    return ""


def get_init(df=df_train, target=None, invalid=None):
    data_map = ib.cal(df, target, invalid)
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
                    row_data[col_name] = "|".join(str(i).encode('utf-8') for i in row[col_name].tolist())
                else:
                    row_data[col_name] = str(row[col_name])
                    if col_name == 'max':
                        row_data['min_boundary'] = row["min"]
                        row_data['max_boundary'] = row["max"]

            subList.append(row_data)
            row_data = collections.OrderedDict()
        out[var_name] = subList
    return out


def get_boundary(out, min_val=0):
    if isinstance(out, dict):
        data = out.items()
    else:
        data = [out]
    for val in data:
        index = 0
        last_bin = None
        for bin_row in val[1]:
            if bin_row["type"] == "Numerical":
                index += 1
                if index == 1:
                    # if float(bin_row["min"]) >= min_val:
                    last_bin = bin_row
                    if float(bin_row["min"]) > min_val:
                        bin_row["min_boundary"] = min_val
                else:
                    if bin_row["min"] != 'nan':
                        last_bin["max_boundary"] = bin_row["min_boundary"]

                        if  len(val[1]) ==index :
                            bin_row["max_boundary"] = 'inf'
                        last_bin = bin_row

                    else:
                        last_bin["max_boundary"] = 'inf'
            else:
                break

    return out


def get_divide_max_bound(out):
    out = get_boundary(out)

    bound = []
    for key, list in out.items():
        for val in list:
            bound.append(float(val["max_boundary"]))
    return bound

def get_divide_min_bound(out):
    out = get_boundary(out)

    bound = []
    for key, list in out.items():
        for val in list:
            bound.append(float(val["min"]))
    return bound

def get_divide_caterotical_bound(out, name):
    bound = []
    for key, list in out.items():
        for val in list:
            s = val[name]
            bound.append(map(cmm.transfer, s.split("|")))
    return bound


def get_merged(var_name, df, min_val):
    """
    adjust方法产生的数据转换成dict.
    如果为numerical,那么每一个bin会额外增加min_bound和max_bound
    min_bound和max_bound的大小取决于min和max.
    唯一区别为min_bound和max_bound在get_boundary中会被调整为连续的,min和max是不连续的.
    后续的adjust的boundary都以min_bound和max_bound的值为标准
    且在get_boundary方法中可以重新划定bound的区间

    Parameters:
        var_name: 变量的名字
        df: pandas dataframe
        min_val: 可以认为指定变量的最小值

    Returns:
        返回一个dict对象

        格式:
        {
        "credit_c_utilization":
        [{
            "bin_num": "0",
            "min": "0.000000000000000",
            "max": "0.377100000000000",
            "min_bound": "0.000000000000000",
            "max_bound": "0.377100000000000",
            "bads": "30",
            "goods": "161",
            "total": "191",
            "total_perc": "19.10%",
            "bad_rate": "15.71%",
            "woe": "0.1157",
            "category_t": "False"
        },
        {..},
        {..}]
}
    """
    data = {var_name: []}
    row_index = 0

    for index, row in df.iterrows():  # 获取每行的index、row
        sub_data = collections.OrderedDict()
        for col_name in df.columns:
            # 如果返回的数据是list,那么转换为字符,并以"|"分割
            if isinstance(row[col_name], list):
                sub_data[col_name] = "|".join(str(i.encode('utf-8')) for i in row[col_name])
            else:
                sub_data[col_name] = str(row[col_name])
            if col_name == 'max':
                # 在max后额外增加两列
                sub_data['min_bound'] = row["min"]
                sub_data['max_bound'] = row["max"]
                if row_index == len(df) - 1:
                    # 在调试当中,adjust之后产生的最后一行数据的bin_num可能会不连续
                    # 这里帮助做处理
                    if [row["bin_num"] - df.iloc[[row_index - 1]]["bin_num"]] > 1:
                        sub_data["bin_num"] = row["bin_num"] - 1
                row_index += 1
        data[var_name].append(sub_data)

        data = get_boundary(data, min_val)
    return data

s = u"nan"
print s


#************************
@app.route(base + "/merge", methods=['POST'])
def merge():
    """归并操作"""
    # 要执行合并的variable
    var_name = request.form.get('varName')
    # 变量的类型
    type = request.form.get('type').encode('utf-8')
    # 选定的范围
    boundary = request.form.get('boundary').encode('utf-8')  # 每个bin_num的max的大小,都以逗号隔开
    # 总的范围
    all_boundary = request.form.get('allBoundary').encode('utf-8')  # 每个bin_num的max的大小,都以逗号隔开
    # 获得target
    # target = request.form.get('allBoundary').encode('utf-8');
    target = request.form.get('target')
    if target is None:
        target ='bad_4w'
    excepted_column = {var_name}

    min_val = 0

    result = None
    type_bool = False
    df = None
    if type == 'Numerical':
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
        #boundary_list.append(np.nan)
        selected_list = boundary_list

        columns = ['bin_num', 'min', 'max', 'min_boundary', 'max_boundary', 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe',
                   'type']
    else:
        type_bool = True
        temp = []
        for s in boundary.split("&"):
            temp.extend(map(cmm.transfer, s.split("|")))

        selected_list = [temp]
        if all_boundary != '':
            for s in all_boundary.split("&"):
                selected_list.append(map(cmm.transfer, s.split("|")))

        columns = ['bin_num', var_name, 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe',
                   'type']

    result = ab.adjust(df_train, type_bool, var_name, selected_list, target=target,
                       expected_column=excepted_column)  # 获得合并的结果
    df = pd.DataFrame(result,
                      columns=columns)

    data = generate_response(var_name, df)
    #data = get_merged(var_name, df, min_val)
    return responseto(data = data)


def generate_response(var_name, df):
    """
    adjust方法产生的数据转换成dict.

    Parameters:
        var_name: 变量的名字
        df: pandas dataframe
        min_val: 可以认为指定变量的最小值

    Returns:
        返回一个dict对象

        格式:
        {
        "credit_c_utilization":
        [{
            "bin_num": "0",
            "min": "0.000000000000000",
            "max": "0.377100000000000",
            "min_boundary": "0.000000000000000",
            "max_boundary": "0.377100000000000",
            "bads": "30",
            "goods": "161",
            "total": "191",
            "total_perc": "19.10%",
            "bad_rate": "15.71%",
            "woe": "0.1157",
            "type": "False"
        },
        {..},
        {..}]
}
    """

    data = {var_name: []}

    for index, row in df.iterrows():  # 获取每行的index、row
        sub_data = collections.OrderedDict()
        for col_name in df.columns:
            # 如果返回的数据是list,那么转换为字符,并以"|"分割
            if isinstance(row[col_name], list):
                sub_data[col_name] = "|".join(str(i.encode('utf-8')) for i in row[col_name])
            else:
                sub_data[col_name] = str(row[col_name])
        data[var_name].append(sub_data)

    return data
