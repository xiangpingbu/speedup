# -*- coding: utf-8 -*-
"""
@author: xiangping
"""

import numpy as np
import pandas as pd


def ks_group_qcut(data, bad, score, group_num, reverse):
    """

    :param data:
    :param bad:
    :param score:
    :param group_num:
    :param reverse:
    :return: KS caculation based on qcut
    """
    data.describe()
    data = data.dropna()
    data['bad'] = data[bad]
    data['score'] = data[score]
    data['good'] = 1 - data.bad

    data['bucket'] = pd.qcut(data.score, group_num)
    grouped = data.groupby('bucket', as_index=False)

    agg1 = grouped.min().score
    agg1 = pd.DataFrame(grouped.min().score, columns=['min_scr'])
    agg1['min_scr'] = grouped.min().score
    agg1['max_scr'] = grouped.max().score
    agg1['bads'] = grouped.sum().bad
    agg1['goods'] = grouped.sum().good
    agg1['total'] = agg1.bads + agg1.goods
    agg1

    # SORT THE DATA FRAME BY SCORE
    agg2 = (agg1.sort_values(by='min_scr', ascending=reverse)).reset_index(drop=True)
    agg2['odds'] = (agg2.goods / agg2.bads).apply('{0:.2f}'.format)
    agg2['bad_rate'] = (agg2.bads / agg2.total).apply('{0:.2%}'.format)

    # CALCULATE CUMULATIVE BAD AND GOOD
    agg2['bad_cum_pct'] = (agg2.bads / data.bad.sum()).cumsum().apply('{0:.2%}'.format)
    agg2['good_cum_pct'] = (agg2.goods / data.good.sum()).cumsum().apply('{0:.2%}'.format)
    # CALCULATE KS STATISTIC
    agg2['ks'] = -np.round(((agg2.bads / data.bad.sum()).cumsum() - (agg2.goods / data.good.sum()).cumsum()), 4) * 100

    # DEFINE A FUNCTION TO FLAG MAX KS
    flag = lambda x: '<----' if x == agg2.ks.max() else ''

    # FLAG OUT MAX KS
    agg2['max_ks'] = agg2.ks.apply(flag)
    return agg2
    #   min_scr  max_scr  bads  goods  total   odds bad_rate     ks max_ks
    # 0      443      620   252    310    562   1.23   44.84%  16.10
    # 1      621      645   201    365    566   1.82   35.51%  26.29
    # 2      646      661   173    359    532   2.08   32.52%  34.04
    # 3      662      677   125    441    566   3.53   22.08%  35.55  <----
    # 4      678      692    99    436    535   4.40   18.50%  34.78
    # 5      693      708    89    469    558   5.27   15.95%  32.36
    # 6      709      725    66    492    558   7.45   11.83%  27.30
    # 7      726      747    42    520    562  12.38    7.47%  19.42
    # 8      748      772    30    507    537  16.90    5.59%  10.72
    # 9      773      848    14    532    546  38.00    2.56%  -0.00


def ks_group_equal(data, bad, score, group_num, reverse):
    """

    :param data:
    :param bad:
    :param score:
    :param group_num:
    :param reverse:
    :return: KS caculation based on equal sample cut
    """
    data['bad'] = data[bad]
    data['score'] = data[score]
    data = data[['bad', 'score']]
    data = data.dropna()
    data = equal_size_bin(data, group_num, reverse)
    data['good'] = 1 - data.bad

    # GROUP THE DATA FRAME BY BUCKETS

    grouped = data.groupby('bucket', as_index=False)

    # CREATE A SUMMARY DATA FRAME

    agg1 = grouped.min().score

    agg1 = pd.DataFrame(grouped.min().score, columns=['min_scr'])

    agg1['min_scr'] = grouped.min().score

    agg1['max_scr'] = grouped.max().score

    agg1['bads'] = grouped.sum().bad

    agg1['goods'] = grouped.sum().good

    agg1['total'] = agg1.bads + agg1.goods

    agg1


    # SORT THE DATA FRAME BY SCORE

    agg2 = (agg1.sort_values(by='min_scr', ascending=reverse)).reset_index(drop=True)

    agg2['odds'] = (agg2.goods / agg2.bads).apply('{0:.2f}'.format)

    agg2['bad_rate'] = (agg2.bads / agg2.total).apply('{0:.2%}'.format)

    # CALCULATE CUMULATIVE BAD AND GOOD
    agg2['bad_cum_pct'] = (agg2.bads / data.bad.sum()).cumsum().apply('{0:.2%}'.format)
    agg2['good_cum_pct'] = (agg2.goods / data.good.sum()).cumsum().apply('{0:.2%}'.format)
    # CALCULATE KS STATISTIC

    agg2['ks'] = -np.round(((agg2.bads / data.bad.sum()).cumsum() - (agg2.goods / data.good.sum()).cumsum()), 4) * 100

    # DEFINE A FUNCTION TO FLAG MAX KS

    flag = lambda x: '<----' if x == agg2.ks.max() else ''

    # FLAG OUT MAX KS

    agg2['max_ks'] = agg2.ks.apply(flag)

    return agg2
    #   min_scr  max_scr  bads  goods  total   odds bad_rate     ks max_ks
    # 0      443      620   252    310    562   1.23   44.84%  16.10
    # 1      621      645   201    365    566   1.82   35.51%  26.29
    # 2      646      661   173    359    532   2.08   32.52%  34.04
    # 3      662      677   125    441    566   3.53   22.08%  35.55  <----
    # 4      678      692    99    436    535   4.40   18.50%  34.78
    # 5      693      708    89    469    558   5.27   15.95%  32.36
    # 6      709      725    66    492    558   7.45   11.83%  27.30
    # 7      726      747    42    520    562  12.38    7.47%  19.42
    # 8      748      772    30    507    537  16.90    5.59%  10.72
    # 9      773      848    14    532    546  38.00    2.56%  -0.00


def equal_size_bin(df_score, group_num, reverse=False):
    """
    :param df_score:
    :param group_num:
    :param reverse:
    :return:
    """
    l = len(df_score)
    step = l/int(group_num)
    df_sort = df_score.sort_values(['score'], ascending=[~reverse])
    df_sort['row_count'] = range(0, l)
    df_sort['bucket'] = df_sort['row_count'].apply(lambda r: r/step)

    return df_sort

