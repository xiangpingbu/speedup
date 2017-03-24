# -*- coding: utf-8 -*-
from rest.app_base import *
from util import Initial_Binning as ib
import pandas as pd
from util import Adjust_Binning as ab
import numpy as np
import collections

base = '/tool'
base_path = "./util/"
# df_test = base_path + "df_test.xlsx"
train_file = base_path + "df_train.xlsx"
df_train = pd.read_excel(train_file)
dataMap = ib.cal(df_train)

merge_record = {}


@app.route(base + "/init")
def init():
    keys = dataMap.keys()
    out = {}
    for k in keys:
        row_data = collections.OrderedDict()
        c = dataMap[k]
        subList = []
        var_name = c[0]
        # var_type = c[1]
        woe_map = c[2]
        boundary = c[3]
        for index, row in woe_map.iterrows():  # 获取每行的index、row
            for col_name in woe_map.columns:
                if isinstance(row[col_name], np.ndarray):
                    row_data[col_name] = row[col_name].tolist()
                else:
                    row_data[col_name] = str(row[col_name])
            subList.append(row_data)
            row_data = collections.OrderedDict()
        out[var_name] = subList

    return responseto(data=out)


@app.route(base + "/merge",methods=['POST'])
def merge():

    var_name = request.form.get('varName').encode('utf-8') #要执行合并的variable
    # var_name = 'credit_c_utilization'
    boundary = request.form.get('boundary').encode('utf-8') #每个bin_num的max的大小,都以逗号隔开
    all_boundary = request.form.get('allBoundary').encode('utf-8') #每个bin_num的max的大小,都以逗号隔开
    # boundary = request.form.get('boundary') #每个bin_num的max的大小,都以逗号隔开
    # boundary_list = [ 0.4013125,0.622375,0.8539396551724138,1.668584905660377,"nan"]
    # boundary_list = [ 0.622375]
    boundary_list = map(eval,boundary.split(","))
    all_boundary_list = []
    for a in all_boundary.split(","):
        if a != 'nan':
            a = float(a)
        else:
            a=np.nan
        all_boundary_list.append(a)
    boundary_list = list(set(all_boundary_list).difference(set(boundary_list)))
    boundary_list.append(np.nan)
    # boundary_list = [ 0.4013125,1.668584905660377,np.nan,np.nan]
    result = ab.adjust(df_train,var_name,boundary_list) #获得合并的结果
    # df_train = result[1]
    data={var_name:[]}
    for index, row in result[0].iterrows():  # 获取每行的index、row
        sub_data= collections.OrderedDict()
        for col_name in result[0].columns:
            if col_name == 'IV' or col_name == 'odds' or col_name == 'ks':
                continue
            sub_data[col_name]= str(row[col_name])
        data[var_name].append(sub_data)
    return responseto(data)

