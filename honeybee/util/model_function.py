# -*- coding: utf-8 -*-
"""
Created on Wed Mar 15 17:29:53 2017

@author: Admin
"""

import pandas as pd
import statsmodels.api as sm
from Model_Selection_Macro import *
# attributes selection


def get_logit_backward(train_x, train_y, target, in_vars=[], in_varpatter='_woe', in_p_value=0.01, in_max_loop=100):
    result = logit_backward(train_x, train_y, vars=in_vars, varpatter=in_varpatter, p_value=in_p_value, max_loop=in_max_loop)
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

    woe_var_list = [x for x in train_x.columns if x.endswith('woe')]
    train_woe_data = train_x[woe_var_list]
    model_para_list = result.params.tolist()
    marginal_var_result = get_marginal_var(train_woe_data, target, model_para_list)
    data['marginal_var'] = marginal_var_result

    return data

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

            # to drop negative woe coef
            var_to_drop = negative_coef_to_drop(result, varpatter=varpatter)
            if (var_to_drop == None):
                print "<MDL SELECTION> DONE"
                next = False
            else:
                print '<RMV VAR> %s' % (var_to_drop)
                del X[var_to_drop]

        else:
            print '<RMV VAR> %s' % (temp_dict.var_name)
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



def get_marginal_var(train_woe_data, target, model_para_list):

    model_para_list = ['credit_query_times_three_woe', 'credit_c_credit_amount_sum_woe', 'credit_utilization_woe', \
                       'personal_education_woe', 'personal_live_case_woe', 'client_gender_woe', \
                       'personal_live_join_woe', 'personal_year_income_woe', 'loan_repayment_frequency_avg_woe', \
                       'age_woe','intercept']


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