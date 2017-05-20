# -*- coding: utf-8 -*-
"""
Created on Wed Dec 28 14:10:23 2016

@author: xiangping
"""

import numpy as np
import pandas as pd


def get_df_summary(df):
    pd.set_option('display.width', 1000)
    pd.set_option('display.max_rows', 10000)
    total_var = df.shape[1]

    df_name = pd.DataFrame(df.columns)
    df_name.rename(columns={0: 'variable'}, inplace=True)

    df_dtypes = pd.DataFrame(df.dtypes)
    df_dtypes.reset_index(inplace=True)
    df_dtypes.rename(columns={'index': 'variable', 0: 'd_type'}, inplace=True)
    df_name = pd.merge(df_name, df_dtypes, on='variable', how='inner')

    df_name['total_cnt'] = df.shape[0]
    df_name['non_missing_cnt'] = 0
    df_name['missing_cnt'] = 0
    df_name['coverage'] = 0
    df_name['uniqueVal'] = 0

    for i in range(len(df_name)):
        col = df_name.iloc[i]['variable']
        df_name.ix[i, 'coverage'] = get_col_coverage(df, col)
        df_name.ix[i, 'uniqueVal'] = get_unique_val(df, col)

    df_name['missing_cnt'] = df_name['total_cnt'] * (1 - df_name['coverage'])
    df_name['non_missing_cnt'] = df_name['total_cnt'] * df_name['coverage']

    df_num = df._get_numeric_data()

    df_min = pd.DataFrame(df_num.min())
    df_min.reset_index(inplace=True)
    df_min.rename(columns={'index': 'variable', 0: 'min'}, inplace=True)
    df_name = pd.merge(df_name, df_min, on='variable', how='left')

    df_max = pd.DataFrame(df_num.max())
    df_max.reset_index(inplace=True)
    df_max.rename(columns={'index': 'variable', 0: 'max'}, inplace=True)
    df_name = pd.merge(df_name, df_max, on='variable', how='left')

    df_mean = pd.DataFrame(df_num.mean())
    df_mean.reset_index(inplace=True)
    df_mean.rename(columns={'index': 'variable', 0: 'mean'}, inplace=True)
    df_name = pd.merge(df_name, df_mean, on='variable', how='left')
    print 'Total Variables: ', total_var, '; Numeric Variables: ', df_num.shape[1], '; Other Variables: ', total_var - \
                                                                                                           df_num.shape[
                                                                                                               1]
    return df_name


def get_col_coverage(df, col, null_value=[]):
    df_tmp = df[[col]].copy()
    tot_len = len(df_tmp)
    # print tot_len
    if len(null_value) > 0:
        df_tmp[col] = np.where(df_tmp[col].isin(null_value), np.nan, df_tmp[col])

    valid_len = len(df[~pd.isnull(df[col])])
    return valid_len * 1.0 / tot_len


def get_unique_val(df, col):
    df_tmp = df[[col]].copy()
    tt = len(df_tmp[col].value_counts())
    return tt
