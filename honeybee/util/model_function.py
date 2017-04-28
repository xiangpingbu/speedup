# -*- coding: utf-8 -*-
"""
Created on Wed Mar 15 17:29:53 2017

@author: Admin
"""

import pandas as pd
import statsmodels.api as sm
from Model_Selection_Macro import *
# attributes selection
import json


def get_logit_backward(train, target, in_vars=[], in_varpatter='_woe', in_p_value=0.01, in_max_loop=100):

    train_y = train[[target]]
    woe_var_list = in_vars
    #woe_var_list = [x for x in train.columns if x.endswith(in_varpatter)]
    train_x = train[woe_var_list]

    result = logit_backward(train_x, train_y, vars=in_vars, p_value=in_p_value, max_loop=in_max_loop)
    """
    data = {}

    model_analysis = {}
    model_analysis['target'] = target
    model_analysis['nobs'] = result.nobs
    model_analysis['df_model'] = result.model.df_model
    model_analysis['df_resid'] = result.model.df_resid
    model_analysis['prsquared'] = result.prsquared
    model_analysis['aic'] = result.aic
    model_analysis['bic'] = result.bic
    model_analysis['likelyhood'] = result.llf
    model_analysis['llnull'] = result.llnull
    model_analysis['llr'] = result.llr_pvalue
    data['model_analysis'] = model_analysis

    selected_var = {}
    selected_var['coef'] = result.params
    selected_var['pvalues'] = result.pvalues
    selected_var['bse'] = result.bse
    selected_var['z'] = result.tvalues
    selected_var['conf_int'] = result.conf_int()
    data['selected_var'] = selected_var


    #woe_var_list = [x for x in train_x.columns if x.endswith('woe')]
    woe_var_list.append(target)
    train_woe_data = train[woe_var_list]
    model_para_list = result.params.tolist()
    marginal_var_result = get_marginal_var(train_woe_data, target, model_para_list)
    data['marginal_var'] = marginal_var_result

    return data
    """

def logit_backward(x, y, vars=[], p_value=0.05, max_loop=100):
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
        var_to_drop = negative_coef_to_drop(result)

        if (temp_dict['p_value'] < p_value):
            # all pvalue are good to go

            # to drop negative woe coef
            var_to_drop = negative_coef_to_drop(result)
            if (var_to_drop == None):
                print "<MDL SELECTION> DONE"
                next = False
            else:
                print '<RMV VAR> %s' % (var_to_drop)
                del X[var_to_drop]

        else:
            print '<RMV VAR> %s' % (temp_dict.var_name)
            del X[temp_dict.var_name]

        count = count + 1
        if (count > max_loop):
            next = False

    return result



def negative_coef_to_drop(mdlresult):
    #
    aa = mdlresult.params.copy()
    aa.sort_values(inplace=True, ascending=True)
    aa = aa[aa<0]

    if len(aa) >0:
        return aa.index[0]
    else:
        return None


def logit_base_model(x, y):
    print '<MDL start>'
    logit = sm.Logit(y, x)
    result = logit.fit()
    #print result.summary2()
    #print result.params
    return result



def get_marginal_var(train_woe_data, target, model_para_list):

    marginal_var_result = Marginal_Selection(train_woe_data, target, model_para_list)
    return marginal_var_result


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

train = pd.read_excel('/Users/xpbu/Documents/Work/maasFile/df_w_woe_all.xlsx')
target = 'bad_4w'

train = train[0:1000]

result = get_logit_backward(train, target, in_vars=[], in_p_value=0.05, in_max_loop=100)
print result.params