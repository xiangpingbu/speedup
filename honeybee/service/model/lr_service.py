# coding=utf-8
import collections

import numpy as np
import pandas

from service import binning_service


def get_init(df, target=None, invalid=None, valid=None, fineMinLeafRate=0.05):
    data_map = binning_service.get_init_bin(df, target, invalid, valid, fineMinLeafRate)
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