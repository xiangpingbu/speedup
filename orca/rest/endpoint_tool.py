# -*- coding: utf-8 -*-
from rest.app_base import *
from util import Initial_Binning as ib
import numpy

base = '/tool'
base_path = "./util/"
df_test = base_path + "df_test.xlsx"
df_train = base_path + "df_train.xlsx"
dataMap = ib.cal(df_train, df_test)


@app.route(base + "/init")
def hello_world():
    keys = dataMap.keys()
    out = {}
    for k in keys:
        data = {}
        c = dataMap[k]
        subList = []
        var_name = c[0]
        # var_type = c[1]
        woe_map = c[2]
        boundary = c[3]
        for index, row in woe_map.iterrows():  # 获取每行的index、row
            for col_name in woe_map.columns:
                if isinstance(row[col_name], numpy.ndarray):
                    data[col_name] = row[col_name].tolist()
                else:
                    data[col_name] = str(row[col_name])
            subList.append(data)
            data = {}
        out[var_name] = subList

    return responseto(data=out)
