# -*- coding: utf-8 -*-
"""
Created on Wed Mar 15 17:29:53 2017

@author: Admin
"""

import pandas as pd
import statsmodels.api as sm

# attributes selection



def logit_backward(x, y, vars=[], varpatter='_woe', p_value=0.05, max_loop=100):
    count = 0
    # decide variables list
    if (len(vars) <= 0):
        vars = x.columns

    if (len(vars) <= 0):
        print '<ERROR>: no variable to select'
        return None

    X = x[vars].copy()
    next = True

    X['intercept'] = 1.0

    while (next):
        print 'logit backward iteration %r' % (count + 1)

        result = logit_base_model(X, y)
        pv = pd.DataFrame(result.pvalues)
        pv.reset_index(inplace=True)
        # print pv.columns
        pv.rename(columns={'index': 'var_name', 0: 'p_value'}, inplace=True)
        # print pv['p_value'].idxmax()
        temp_dict = pv.ix[pv['p_value'].idxmax()]
        # dfrm.ix[dfrm['A'].idxmax()]


        if (temp_dict['p_value'] < p_value):
            # all pvalue are good to go

            # to drop positive woe coef
            var_to_drop = positive_coef_to_drop(result, varpatter=varpatter)
            if (var_to_drop == None):
                print "<MDL SELECTION> DONE"
                next = False
            else:
                print '<RMV VAR> %s' % str(var_to_drop)
                del X[var_to_drop]


                # return result
        else:
            print '<RMV VAR> %s' % str(temp_dict.var_name)
            del X[str(temp_dict.var_name)]

        count = count + 1
        if (count > max_loop):
            next = False

    return result


def positive_coef_to_drop(mdlresult, varpatter='_woe'):
    #
    aa = mdlresult.params.copy()
    aa.sort_values(inplace=True, ascending=0)
    Found = False
    for i in aa[aa > 0].index:
        if (str(i).find(varpatter) != -1):
            Found = True
            break
    if Found:
        return str(i)
    else:
        return None


def negative_coef_to_drop(mdlresult, varpatter='_woe'):

    #
    aa = mdlresult.params.copy()
    aa.sort_values(inplace=True, ascending=0)
    Found = False
    for i in aa[aa < 0].index:
        if (str(i).find(varpatter) != -1):
            Found = True
            break
    if Found:
        return str(i)
    else:
        return None


def logit_base_model(x, y):
    print '<MDL start>'
    logit = sm.Logit(y, x)
    result = logit.fit()
    print result.summary2()
    return result


train_woe_data = pd.read_excel('/Users/xpbu/Documents/Work/maasFile/train_woe_data.xlsx')

train_y = train_woe_data[['bad_7mon_60']]
train_x = train_woe_data.drop(['apply_id', 'bad_7mon_60'], axis=1)

result = logit_backward(train_x, train_y, vars=[], varpatter='_woe', p_value=0.01, max_loop=100)


# remove high correlation variables
'''
def check_corr(x, y, corr_cap=0.75):
    print 'checking corration'
    base_col = set(x.columns)
    corr = x.corr()
    base_col = base_col.difference(set(['Intercept', 'intercept']))
    base_col_list = list(base_col)
    corr_pair = []
    df_f = pd.DataFrame(y)
    df_f.columns = ['target']
    df = pd.concat([x, df_f], axis=1)
    iv_dict = {}
    for i in base_col_list:
        uniqV = len(x[i].value_counts())
        iv_dict[i] = getIV(df, i, 'target', 'num', uniqV)

    base_col_list = list(base_col)
    for i in range(len(base_col_list)):
        for j in range(i + 1, len(base_col_list)):
            if abs(corr[base_col_list[i]][base_col_list[j]] >= corr_cap):
                # compare IV
                if (iv_dict[base_col_list[i]] >= iv_dict[base_col_list[j]]):
                    to_add = base_col_list[i]
                    to_remove = base_col_list[j]
                else:
                    to_add = base_col_list[j]
                    to_remove = base_col_list[i]

                # choose big one into set and remove lower one
                base_col = base_col.difference(set([to_remove]))
                base_col = base_col.union(set([to_add]))
    print 'Done with ckecking corration!'

    return list(base_col)
'''