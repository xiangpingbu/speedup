# coding=utf-8
"""
@author: lifeng
"""

import sys
reload(sys)
sys.setdefaultencoding('utf-8')


def transfer(str):
    """将字符串转化为utf8"""
    return str.encode('utf-8')


def replace(str):
    """将字符串中的|替换为,"""
    return str.replace("|",",")


def df_for_html(df):
    data_map = {}
    head = []
    for column in df.columns:
        head.append(str(column))

    data_map["body"] = []
    for index,row in df.iterrows():
        body = []
        data_map["body"].append(body)
        for key in head:
            body.append(str(row[key]))

    data_map["head"] = head

    return data_map

