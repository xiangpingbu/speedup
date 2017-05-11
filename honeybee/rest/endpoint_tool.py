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
import sys
from util import model_function
import requests
from common.constant import  const
from util.ZipFile import *


base = '/tool'
base_path = "./util/"


def file_init():
    train_file = base_path + "df_train.xlsx"
    train = pd.read_excel(train_file)

    test_file = base_path + "df_test.xlsx"
    test = pd.read_excel(test_file)
    return [train, test]



model_name = "model_train_selected"

# df_train = pd.read_excel("/Users/xpbu/Documents/Work/maasFile/df_train.xlsx")
df_train = pd.read_excel("/Users/lifeng/Desktop/pailie/model_train_selected2.xlsx")
# df_train = None
# df_test = pd.read_excel("/Users/lifeng/Desktop/df_test.xlsx")
# df_test = pd.read_excel("/Users/xpbu/Documents/Work/maasFile/df_test.xlsx")
df_test = pd.read_excel("/Users/lifeng/Desktop/pailie/model_test_selected2.xlsx")
# df_test = None
safely_apply = False
apply_result = None


@app.route(base + "/init", methods=['POST'])
def init():
    name = request.form.get("model_name")
    branch = request.form.get("branch")

    if name is None or name == '':
        name = model_name
        branch = "master"

    result = vs.load_branch(name,branch)

    # if var_service.if_branch_exist(model, branch):
    #     var_service.update_branch(model, branch, remove_list, selected_list)
    # else:
    #     var_service.create_branch(model, branch, target, remove_list, selected_list)

    remove_list_json = json.loads(result[0]["remove_list"])
    remove_list = []
    for o in remove_list_json :
        remove_list.append(o)

    # remove_list.append(target)

    # invalid = invalid.split(",")
    # min = request.form.get("min")
    min_val = 0
    df = df_train
    init_result = get_init(df, target=result[0]["model_target"], invalid=remove_list)

    out = get_boundary(init_result, min_val)
    # for
    # first_bin = val[0]
    # if first_bin["category_t"] == False:
    #     val[0]["min"] = min_val
    out_sorted_iv= sort_iv(out)
    return responseto(data=out_sorted_iv)


# @app.route(base + "/merge", methods=['POST'])
# def merge():
#     """归并操作"""
#     # 要执行合并的variable
#     var_name = request.form.get('varName')
#     # 变量的类型
#     type = request.form.get('type').encode('utf-8')
#     # 选定的范围
#     boundary = request.form.get('boundary').encode('utf-8')  # 每个bin_num的max的大小,都以逗号隔开
#     # 总的范围
#     all_boundary = request.form.get('allBoundary').encode('utf-8')  # 每个bin_num的max的大小,都以逗号隔开
#     # 获得target
#     # target = request.form.get('allBoundary').encode('utf-8');
#     target = request.form.get('target')
#     if target is None:
#         target = 'bad_4w'
#     excepted_column = {var_name}
#
#     min_val = 0
#
#     result = None
#     type_bool = False
#     df = None
#     if type == 'False':
#         # 将字符转换为list
#         boundary_list = map(eval, boundary.split("&"))
#         all_boundary_list = []
#         # 将字符转换为list,nan替换为np.nan
#         for a in all_boundary.split("&"):
#             if a != 'nan':
#                 a = float(a)
#             else:
#                 a = np.nan
#             all_boundary_list.append(a)
#         boundary_list = list(set(all_boundary_list).difference(set(boundary_list)))
#         boundary_list.append(np.nan)
#         selected_list = boundary_list
#
#         columns = ['bin_num', 'min', 'max', 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe',
#                    'category_t']
#     else:
#         type_bool = True
#         temp = []
#         for s in boundary.split("&"):
#             temp.extend(map(cmm.transfer, s.split("|")))
#
#         selected_list = [temp]
#         for s in all_boundary.split("&"):
#             selected_list.append(map(cmm.transfer, s.split("|")))
#
#         columns = ['bin_num', var_name, 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe',
#                    'category_t']
#
#     result = ab.adjust(df_train, type_bool, var_name, selected_list, target=target,
#                        expected_column=excepted_column)  # 获得合并的结果
#     df = pd.DataFrame(result[0],
#                       columns=columns)
#
#     data = get_merged(var_name, df, min_val)
#     return responseto(data=data)


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
    target = request.form.get("target")
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

        out = get_init(df, target=target, invalid=[], fineMinLeafRate=0)
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
        iv = result['IV'].sum()
        df = pd.DataFrame(result,
                          columns=columns)
        data = generate_response(name, df, iv)
        #data = get_merged(name, df, min_val)

        return responseto(data=data)

    else:
        val = data_map["selected"][name].split("|")
        # for index, row in df.iterrows():
        #     if row[name] in val:
        #         pass
        #     else:
        #         df.drop(index, inplace=True)

        df[name] = df[name].apply(lambda x: float_nan_to_str_nan(x))

        df = df[df[name].isin(val)]

        list = data_map["table"]
        # 删除要被分裂的项
        del list[data_map["selectedIndex"]]

        out = get_init(df, target=target, invalid=[], fineMinLeafRate=0)
        bound_list = get_divide_caterotical_bound(out, name)
        # 被分裂的项的下标
        index = data_map["selectedIndex"]
        # 将分裂的结果加入原有的列表中
        for v in list:
            bound_list.append(map(cmm.transfer, v[name].split("|")))
        result = ab.adjust(df_train, data_map["selected"]["type"] == 'Categorical', name, bound_list
                           ,target=target, expected_column={name})
        iv = result['IV'].sum()
        columns = ['bin_num', name, 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe',
                   'type']
        df = pd.DataFrame(result,
                          columns=columns)

        data = generate_response(name, df,iv)
        #data = get_merged(name, df, min_val)
        return responseto(data = data)

@app.route(base + "/divide_manually",methods=['POST'])
def divide_manually():
    boundary = request.form.get("boundary")
    variable_name = request.form.get("variable_name")
    branch = request.form.get("branch")
    model_name = request.form.get("model_name")
    type = request.form.get("type")

    boundary_list = []
    if type =="true":
        for s in boundary.split(","):
            temp = []
            temp.extend(map(cmm.transfer, s.split("|")))
            boundary_list.append(temp)
        columns = ['bin_num', variable_name, 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe',
                   'type']

    else :
        for s in boundary.split(","):
            boundary_list.append(float(s))
        columns = ['bin_num', 'min', 'max', 'min_boundary', 'max_boundary', 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe',
                   'type']

    target = vs.load_branch(model_name,branch)[0]["model_target"]
    result = ab.adjust(df_train, type =="true", variable_name, boundary_list
                       , target=target, expected_column={variable_name})

    iv = result['IV'].sum()
    df = pd.DataFrame(result,
                      columns=columns)
    data = generate_response(variable_name, df, iv)
    return responseto(data=data)


@app.route(base + "/apply", methods=['POST'])
def apply():
    """将train数据得到的woe与test数据进行匹配"""
    req = request.form.get('data')
    var_dict = json.loads(req)

    data = var_dict["data"]

    df = df_test.append(df_train)
    # df = df_train.copy()
    var_list = data.keys()

    for var_name in var_list:
        df[var_name+'_woe'] = df[var_name].apply(lambda var_value: apply_get_woe_value(var_name, var_value, data))

    global apply_result,safely_apply
    apply_result = df
    safely_apply = True
    output = BytesIO()
    writer = pd.ExcelWriter(output, engine='xlsxwriter')
    df.to_excel(writer, startrow=0, merge_cells=False, sheet_name="Sheet_1")
    workbook = writer.book
    worksheet = writer.sheets["Sheet_1"]
    format = workbook.add_format()
    format.set_bg_color('#eeeeee')
    worksheet.set_column(0, 9, 28)
    writer.close()

    output.seek(0)
    response = make_response(send_file(output, attachment_filename="df_iv.xlsx", as_attachment=True))
    return responsePandas(response)


def isNum(v):
    try:
        val = float(v)
        return True
    except ValueError:
        return False

def apply_get_woe_value(var_name, var_value, var_dict):
    var_content = var_dict[var_name]
    var_type = var_content[0]['type']
    if var_type == 'Numerical':
        if not isNum(var_value):
            return 0.0
        for row in var_content:
            if row['min_boundary'] == '-inf':
                min_boundary = sys.float_info.min
            else:
                min_boundary = float(row['min_boundary'])

            if row['max_boundary'] == 'inf':
                max_boundary = sys.float_info.max
            else:
                max_boundary = float(row['max_boundary'])

            if np.isnan(var_value) and str(min_boundary) == 'nan':
                return float(row['woe'])
            elif min_boundary <= var_value < max_boundary:
                return float(row['woe'])
        return 0.0
    else:
        for row in var_content:
            if str(var_value) in row[var_name]:
                return float(row['woe'])
        return 0.0


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

            print filename
            if filename.find("test") >0:
                df_test = pd.read_excel(file, encoding="utf-8")
            elif filename == 'df_train.xlsx':
                df_train = pd.read_excel(file, encoding="utf-8")
                if filename.find("_")>0:
                    model_name = filename.split("_")[0]
                else:
                    model_name = "anonymous"
    return responseto(data="success")


@app.route(base + "/parse", methods=['GET'])
def parse():
    df = a99.GetDFSummary(df_train)
    data_map = cmm.df_for_html(df)
    result = vs.load_model(model_name)
    if len(result) < 1:
        vs.create_branch(model_name, "master", None, None)
        result = []
        result.append({"model_branch":"master"})

    branches = []

    # 只取master
    v = result[0]
    remove_list = ""
    if v["remove_list"] is not None:
        remove_list = v["remove_list"]
        data_map["target"] = v["model_target"]


    for n in result:
        branches.append(n["model_branch"])

    data_map["current_model"] = model_name
    data_map["branches"] = branches
    data_map["remove_list"] = remove_list
    return responseto(data=data_map)


@app.route(base + "/column_config", methods=['POST'])
def column_config():
    """
    将配置完成的variable数据转化一定格式的json数据
    这些数据会用于生成pmml

    Parameters:
        data: variable的的行列信息
    """
    req = request.form.get('data')
    var_dict = json.loads(req)

    list = var_dict['list']
    model_name = var_dict['model_name']
    model_branch = var_dict['model_branch']
    params = var_dict["params"]
    result = sort_variable(list.split(","),vs.load_binning_record(model_name,model_branch,list.split(",")))
    data = []
    mem_zip_file = MemoryZipFile()
    for variable in result:
        # list = result.copy
        records = json.loads(variable["binning_record"],encoding="utf8")
        first_row = records[0]
        #如果type为true,那么为Numrical
        type = first_row["type"] == 'Numerical'
        #如果bin_num为0,那么这一行woe值为missing值
        if first_row["bin_num"] == '0' and type:
            missing_woe = first_row["woe"]
            del records[0]
        else:
            missing_woe = 0
        variable_name = variable["variable_name"]
        columnBinning = {"binCountNeg": [],
                         "binCountPos": [],
                         "binWeightedPos": [],
                         "binWeightedWoe": [],
                         "binAvgScore": [],
                         "binWeightedNeg": [],
                         "binPosRate": []}
        pmml = Pmml()
        pmml.columnFlag = None
        pmml.finalSelect = True
        pmml.columnName = variable_name
        pmml.columnBinning = columnBinning


        if type:
            pmml.columnType = "N"
            columnBinning["binBoundary"] = ["-Infinity"]
            columnBinning["binCategory"] = None
            #0指代invalid的值
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
                if index == len(records)-1:
                    columnBinning["binCountWoe"].append(float(missing_woe))
            else:
                # categorical的woe值
                # for cate in records:
                v_list= val[variable_name.decode('utf-8')].split("|")
                for v in v_list:
                    if v != 'nan':
                        columnBinning["binCategory"].append(v)
                        columnBinning["binCountWoe"].append(val["woe"])
                    else:
                        categorical_nan_woe =val['woe']
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

    column_config = json.dumps(data,ensure_ascii=False)
    post_data = {"column_config":json.dumps(data,ensure_ascii=False),
                     "params":params}
    pmml_xml =requests.post(const.MAAS_HOST + "/rest/pmml/generate", data=post_data).text
    mem_zip_file.append_content('column_config/column_config.json',column_config)
    mem_zip_file.append_content('column_config/model.pmml',pmml_xml)

    # return responseFile(make_response(mem_zip_file),"config.zip")
    return send_file(mem_zip_file.read(),attachment_filename='config.zip',as_attachment=True)

# column_config("model_train_selected","xiaozhuo","管理岗位,call_cnt")
@app.route(base + "/column_config2", methods=['get'])
def column_config2():
    mem_zip_file = MemoryZipFile()
    mem_zip_file.append_content('column_config/column_config.json',"1")
    mem_zip_file.append_content('column_config/model.pmml',"2")


    return send_file(mem_zip_file.read(),attachment_filename='capsule.zip',as_attachment=True)


def get_init(df=df_train, target=None, invalid=None, fineMinLeafRate=0.05):
    data_map = ib.cal(df, target, invalid, fineMinLeafRate)
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
        iv = c[4]
        var_content = collections.OrderedDict()
        var_content['iv'] = iv
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
        var_content['var_table'] = subList
        out[var_name] = var_content
    return out

'''
def get_boundary(out, min_val=0):
    if isinstance(out, dict):
        data = out.items()
    else:
        data = [out]

    for val in data:
        index = 0
        last_bin = None
        for bin_row in val[1]['var_table']:
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
'''

#有时间的话， 要做优化修改
def get_boundary(out, min_val=0):
    if isinstance(out, dict):
        data = out.items()
    else:
        data = [out]

    for val in data:
        index = 0
        last_bin = None
        for i, bin_row in enumerate(val[1]['var_table']):
            index += 1
            if bin_row["type"] == "Numerical":
                if i == 0 and bin_row["min"] == 'nan':
                    bin_row["max_boundary"] = 'nan'
                else:
                    if index == 1:
                        # if float(bin_row["min"]) >= min_val:
                        bin_row["min_boundary"] = min_val
                        if i == (len(val[1]['var_table'])-1):
                            bin_row["max_boundary"] = 'inf'
                    else:
                        last_bin["max_boundary"] = bin_row["min_boundary"]
                        if i == (len(val[1]['var_table'])-1):
                            bin_row["max_boundary"] = 'inf'
                last_bin = bin_row
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
    for key, var_content in out.items():
        row_list = var_content['var_table']
        for val in row_list:
            bound.append(float(val["min"]))
    return bound

'''
out格式
{
 key:{
    iv:{}
    var_table:[]
 }
}
'''

def get_divide_caterotical_bound(out, name):
    bound = []
    for key, list in out.items():
        for val in list["var_table"]:
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
    iv = result['IV'].sum()

    df = pd.DataFrame(result,
                      columns=columns)

    data = generate_response(var_name, df, iv)
    #data = get_merged(var_name, df, min_val)
    return responseto(data = data)

'''
apply完成后,第一次进入时的变量选择
'''
@app.route(base+"/variable_select",methods=['POST'])
def variable_select():
    var_list = request.form.get("var_list")
    target = request.form.get("target")

    data = model_function.get_logit_backward(apply_result,target,20,var_list.split(","))
    if data is None:
        return responseto(success=False)
    return responseto(data=data)

'''
手动选择变量
'''
@app.route(base+"/variable_select_manual",methods=['POST'])
def variable_select_manual():
    all_list = request.form.get("all_list")
    selected_list = request.form.get("selected_list")
    target = request.form.get("target")
    data = model_function.get_logit_manual(apply_result,all_list.split(","),selected_list.split(","),target,20)
    return responseto(data=data)

'''
导出变量配置
'''
@app.route(base+"/export",methods=['POST'])
def export_variables():
    data = request.form.get("data")
    response = make_response(data)
    return responseFile(response,"variable_config.json")
'''
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
    '''
def generate_response(var_name, df, iv):
    #data = {var_name: []}
    data = collections.OrderedDict()
    var_content = collections.OrderedDict()
    var_content['iv'] = iv
    var_content['var_table'] = []
    for index, row in df.iterrows():  # 获取每行的index、row
        sub_data = collections.OrderedDict()
        for col_name in df.columns:
            # 如果返回的数据是list,那么转换为字符,并以"|"分割
            if isinstance(row[col_name], list):
                sub_data[col_name] = "|".join(str(i.encode('utf-8')) for i in row[col_name])
            else:
                sub_data[col_name] = str(row[col_name])
        var_content['var_table'].append(sub_data)

    data[var_name] = var_content
    return data


def sort_iv(out):
    out_sorted_iv = OrderedDict(sorted(out.items(), key=lambda v: v[1]['iv'], reverse=True))
    return out_sorted_iv

def float_nan_to_str_nan(x):
    if type(x) == float:
        return str(x)
    else:
        return x

def sort_variable(variables,result):
    v = {}
    for index,name in enumerate(variables):
        v[name.decode('utf-8')] = index

    new_result = [{}] * (len(v)-1)
    for variable in result:
        i = v[variable["variable_name"].decode('utf-8')]
        new_result[i] = variable

    return new_result
# variables = ["性别","年龄"]
# result = vs.load_binning_record("model_train_selected","xiaozhuo",variables)
# sort_variable(variables,result)





