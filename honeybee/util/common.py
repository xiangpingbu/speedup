# coding=utf-8
"""
@author: lifeng
"""

import sys
import pandas as pd
from service import binning_service as bs

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


def get_iv_tree_binning(df, target):
    """
    :param df:
    :param target:
    :return: IV dict of the dataframe based on tree-based dynamic binning
    """

    iv_dict = {}
    var_list = df.columns
    for v in var_list:
        v_type = df[v].dtype
        v_iv = bs.get_tree_bin(df, v, target, v_type)['IV']
        iv_dict[v] = v_iv

    return iv_dict


def is_valid_correlation(df, corr_cap=1):
    var_corr = df.corr()
    for col in var_corr.columns:
        for row in var_corr.index:
            if col != row and var_corr[col][row] >= corr_cap:
                return False

    return True


def get_correlation(df):
    return df.corr()


def avoid_singular_matrix_error(df):
    """

    :param df:
    :return: return a df to avoid singular matrix error in the next step
    """
    #   remove the all ) col
    new_df = df.loc[:, (df != 0).any(axis=0)]

    #   remove the variables with highly correlation value
    #   leave to user's operation



# def remove_high_corr(df, target, corr_cap=0.99):
#     """
#     remove variable with corr_cap
#     :param df:
#     :param target:
#     :param corr_cap:
#     :return:
#     """
#     var_corr = df.corr()
#     iv_dict = {}
#     var_set = ()
#     iv_dict = get_iv_tree_binning(df, target)
#     for col in var_corr.columns:
#         for row in var_corr.index:
#             if col != row and var_corr[col][row] > corr_cap:
#
#             else:
#                 var_set.add(col)
#
#
#     base_col_list=list(base_col)
#     for i in range(len(base_col_lisxt)):
#         for j in range(i+1,len(base_col_list)):
#             if abs(corr[base_col_list[i]][base_col_list[j]]>=corr_cap):
#                 #compare IV
#                 if (iv_dict[base_col_list[i]]>=iv_dict[base_col_list[j]]):
#                     to_add=base_col_list[i]
#                     to_remove=base_col_list[j]
#                 else:
#                     to_add=base_col_list[j]
#                     to_remove=base_col_list[i]
#
#                 # choose big one into set and remove lower one
#                 base_col=base_col.difference(set([to_remove]))
#                 base_col=base_col.union(set([to_add]))
#     print 'Done with ckecking corration!'
#
#     return list(base_col)