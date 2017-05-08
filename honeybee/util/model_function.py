# -*- coding: utf-8 -*-
"""
Created on Wed Mar 15 17:29:53 2017

@author: Admin
"""



from Model_Selection_Macro import *


# attributes selection
import json


def get_logit_backward(train, target, in_vars=[], in_p_value=0.01, in_max_loop=100):

    train_y = train[[target]]
    #woe_var_list = in_vars
    woe_var_list = [x+'_woe' for x in in_vars]
    woe_var_list_with_target = [x for x in woe_var_list]
    woe_var_list_with_target.append(target)
    train_x = train[woe_var_list]
    train_x_with_target = train[woe_var_list_with_target]

    result = logit_backward(train_x, train_y, vars=woe_var_list, p_value=in_p_value, max_loop=in_max_loop)
    #print result.params

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
    params = result.params
    pvalues = result.pvalues
    bse = result.bse
    tvalues = result.tvalues
    conf_int = result.conf_int()
    selected_var = pd.DataFrame([params, pvalues, bse, tvalues, conf_int[0], conf_int[1]])
    selected_var = selected_var.transpose()
    selected_var.columns = ['params','pvalues','bse', 'tvalues', 'conf_int0', 'conf_int1']
    selected_var['combine'] = selected_var.apply(lambda v: ','.join(str(x) for x in v.tolist()), axis=1)
    name = selected_var.index.tolist()
    combine = selected_var['combine'].tolist()
    var_result = zip(name, combine)
    #selected_var['coef'] = result.params
    #selected_var['pvalues'] = result.pvalues
    #selected_var['bse'] = result.bse
    #selected_var['z'] = result.tvalues
    #selected_var['conf_int'] = result.conf_int()

    data['selected_var'] = var_result


    #woe_var_list = [x for x in train_x.columns if x.endswith('woe')]
    woe_var_list.append(target)
    #train_woe_data = train[woe_var_list]
    model_para_list = result.params.index.tolist()
    marginal_var_result = get_marginal_var(train_x_with_target, target, model_para_list)
    marginal_var_result['combine'] = marginal_var_result[['KS', 'P_Value']].apply(lambda v: ','.join(str(x) for x in v), axis=1)
    margin_name = marginal_var_result['var_name']
    margin_combine = marginal_var_result['combine'].tolist()
    margin_result = zip(margin_name, margin_combine)
    data['marginal_var'] = margin_result

    return data



def logit_backward(x, y, vars=[], p_value=0.05, max_loop=100):
    count = 0

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


def get_marginal_var(train_woe_data, target, model_para_list):

    marginal_var_result = Marginal_Selection(train_woe_data, target, model_para_list)
    return marginal_var_result


train = pd.read_excel('/Users/xpbu/Documents/Work/maasFile/df_w_woe_all.xlsx')
target = 'bad_4w'
selected = [u'cell_operator',
            u'province',
            u'cell_loc',
            u'cell_operator_zh',
            u'信用评分_1',
            u'contacts_class1_cnt',
            u'phone_gray_score',
            u'芝麻信用',
            u'花呗金额',
            u'公司性质',
            u'call_in_cnt',
            u'居住情况',
            u'call_cnt',
            u'total_amount',
            u'学历',
            u'call_in_time',
            u'call_out_cnt',
            u'工作年限',
            u'手机入网时间',
            u'中高级职称']
result = get_logit_backward(train, target, in_vars=selected, in_p_value=0.05, in_max_loop=100)


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