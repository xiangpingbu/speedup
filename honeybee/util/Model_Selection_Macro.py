# -*- coding: utf-8 -*-
import pandas as pd
import numpy as np
import statsmodels.api as sm
import random

def LogisticReg_KS(df, target, para_list):
    import statsmodels.api as sm
    ############# re-train the model #####################
    print para_list
    result_temp = logit_base_model(df[para_list], df[target])
    #logit = sm.Logit(df[target], df[para_list])
    #result_temp = logit.fit()
    if result_temp is None:
        return [None, None]
    p = result_temp.summary2()
    p_value = p.tables[1][u'P>|z|'][-1]
    #    result_temp.summary()
    #    result_temp.params
    ##################### get score for training set ######################
    df['prob_bad'] = result_temp.predict(df[para_list])
    ###################### calculate KS ##############################
    #    ks_group(train_woe_data,target, 'prob_bad', 20, True)
    ks = ks_group(df, target, 'prob_bad', 20, True).ks.max()
    #####################################################################
    return [ks, p_value]


def Marginal_Selection(df, target, model_para_list):
    all_para_list = df.columns.tolist()
    step2_list = [x for x in all_para_list if x not in model_para_list and x not in [target, 'apply_id']]
    result = pd.DataFrame()
    for var in step2_list:
        model_para_list_add = model_para_list + [var]
        [ks_temp, p_value] = LogisticReg_KS(df, target, model_para_list_add)
        result_temp = pd.DataFrame({"var_name": var, "KS": ks_temp, "P_Value": p_value}, index=["0"])
        result = result.append(result_temp)
    result.sort_values('KS', ascending=False, inplace=True)
    return result


def ks_group(data, bad, score, group_num, reverse):
    import pandas as pd
    import numpy as np

    #    data = pd.read_excel("test_dataset.xlsx")
    #    data = pd.read_csv('Credit Card Default Forecast 0.csv')

    data.describe()
    data = data.dropna()
    #    data.bad = data[bad]
    #    data.score = data[score]
    data['bad'] = data[bad]
    data['score'] = data[score]

    #               bad        score
    # count  5522.000000  5522.000000
    # mean      0.197573   693.466135
    # std       0.398205    57.829769
    # min       0.000000   443.000000
    # 25%       0.000000   653.000000
    # 50%       0.000000   692.500000
    # 75%       0.000000   735.000000
    # max       1.000000   848.00000078

    data['good'] = 1 - data.bad

    # DEFINE 10 BUCKETS WITH EQUAL SIZE

    data['bucket'] = pd.qcut(data.score, group_num)

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

    #   min_scr  max_scr  bads  goods  total
    # 0      621      645   201    365    566
    # 1      646      661   173    359    532
    # 2      662      677   125    441    566
    # 3      678      692    99    436    535
    # 4      693      708    89    469    558
    # 5      709      725    66    492    558
    # 6      726      747    42    520    562
    # 7      748      772    30    507    537
    # 8      773      848    14    532    546
    # 9      443      620   252    310    562

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

def logit_base_model(x, y, try_cnt=1):
    print '<MDL start>'
    print 'try count: ', try_cnt
    if try_cnt > 10:
        print 'exceed 10 tries, stop !'
        return None
    try:
        logit = sm.Logit(y, x)
        result = logit.fit()
        return result
    except np.linalg.LinAlgError:
        s_x = variable_order_shuffle(x)
        return logit_base_model(s_x, y, try_cnt+1)
    except Exception, e:
        print e
        return None

def variable_order_shuffle(df):
    c_list = df.columns.tolist()
    random.shuffle(c_list)
    new_df = df[c_list]
    return new_df