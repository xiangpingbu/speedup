# -*- coding: utf-8 -*-
"""
Created on Wed Mar 15 17:29:53 2017

@author: Admin
"""

from Model_Selection_Macro import *


def get_logit_manual(train, all_list, selected_list, target, ks_group_num):

    train_y = train[[target]]
    woe_var_list = [x+'_woe' for x in selected_list]
    train_x = train[woe_var_list]
    result = logit_base_model(train_x, train_y)
    if result is None:
        return None

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

    params = result.params
    pvalues = result.pvalues
    bse = result.bse
    tvalues = result.tvalues
    conf_int = result.conf_int()
    selected_var = pd.DataFrame([params, pvalues, bse, tvalues, conf_int[0], conf_int[1]])
    selected_var = selected_var.transpose()
    selected_var.columns = ['params', 'pvalues', 'bse', 'tvalues', 'conf_int0', 'conf_int1']
    selected_var['combine'] = selected_var.apply(lambda v: ','.join(str(x) for x in v.tolist()), axis=1)
    name = selected_var.index.tolist()
    combine = selected_var['combine'].tolist()
    var_result = zip(name, combine)
    data['selected_var'] = var_result

    all_var_list_with_target = [x+'_woe' for x in all_list]
    all_var_list_with_target.append(target)
    train_x_with_target = train[all_var_list_with_target]

    model_para_list = result.params.index.tolist()
    marginal_var_result = get_marginal_var(train_x_with_target, target, model_para_list, ks_group_num)
    if marginal_var_result is not None:
        marginal_var_result['combine'] = marginal_var_result[['KS', 'P_Value']].apply(lambda v: ','.join(str(x) for x in v), axis=1)
        margin_name = marginal_var_result['var_name']
        margin_combine = marginal_var_result['combine'].tolist()
        margin_result = zip(margin_name, margin_combine)
        data['marginal_var'] = margin_result
    else:
        data['marginal_var'] = None
    print data
    return data


def get_logit_backward(train, target, ks_group_num, in_vars=[], in_p_value=0.01, in_max_loop=100):

    train_y = train[[target]]
    #woe_var_list = in_vars
    woe_var_list = [x+'_woe' for x in in_vars]
    woe_var_list_with_target = [x for x in woe_var_list]
    woe_var_list_with_target.append(target)
    train_x = train[woe_var_list]
    train_x_with_target = train[woe_var_list_with_target]

    result = logit_backward(train_x, train_y, vars=woe_var_list, p_value=in_p_value, max_loop=in_max_loop)
    if result is None:
        return None
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
    marginal_var_result = get_marginal_var(train_x_with_target, target, model_para_list, ks_group_num)
    if marginal_var_result is not None:
        marginal_var_result['combine'] = marginal_var_result[['KS', 'P_Value']].apply(lambda v: ','.join(str(x) for x in v), axis=1)
        margin_name = marginal_var_result['var_name']
        margin_combine = marginal_var_result['combine'].tolist()
        margin_result = zip(margin_name, margin_combine)
        data['marginal_var'] = margin_result
    else:
        data['marginal_var'] = None

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


def get_marginal_var(train_woe_data, target, model_para_list, ks_group_num):

    marginal_var_result = Marginal_Selection(train_woe_data, target, model_para_list, ks_group_num)
    return marginal_var_result


# train = pd.read_excel('/Users/xpbu/Documents/Work/maasFile/df_w_woe_all.xlsx')
target = 'bad_4w'
'''
all_list = [u'cell_operator',
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
'''
all_list = [u'cell_operator',
            u'province',
            u'cell_loc',
            u'cell_operator_zh',
            u'信用评分_1',
            u'花呗金额',
            u'call_out_cnt']
selected_list = [u'cell_operator',
                 u'province',
                 u'cell_loc',
                 u'cell_operator_zh',
                 u'信用评分_1',
                 u'花呗金额']
ks_group_num = 20
# result = get_logit_manual(train, all_list, selected_list, target, ks_group_num)
