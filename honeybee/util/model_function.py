# -*- coding: utf-8 -*-
"""
Created on Wed Mar 15 17:29:53 2017

@author: Admin
"""

from Model_Selection_Macro import *


def get_logit_backward_manually(train_woe, test_woe, all_list, selected_list, target, ks_group_num=20,
                                withIntercept=True):
    train_y = train_woe[[target]]
    if 'intercept' in selected_list: selected_list.remove('intercept')
    if 'intercept' in all_list: all_list.remove('intercept')
    woe_var_list = [x + '_woe' for x in selected_list]

    if withIntercept:
        woe_var_list.append('intercept')
        train_woe['intercept'] = 1.0
        test_woe['intercept'] = 1.0

    train_x = train_woe[woe_var_list]

    train_result = logit_base_model(train_x, train_y)
    if train_result is None:
        return None

    data = {}

    model_analysis = {}
    model_analysis['target'] = target
    model_analysis['nobs'] = str(train_result.nobs)
    model_analysis['df_model'] = 'logit'
    model_analysis['df_resid'] = str(train_result.model.df_resid)
    model_analysis['prsquared'] = str(train_result.prsquared)
    model_analysis['aic'] = str(train_result.aic)
    model_analysis['bic'] = str(train_result.bic)
    model_analysis['likelyhood'] = str(train_result.llf)
    model_analysis['llnull'] = str(train_result.llnull)
    model_analysis['llr'] = str(train_result.llr_pvalue)
    data['model_analysis'] = model_analysis

    params = train_result.params
    pvalues = train_result.pvalues
    bse = train_result.bse
    tvalues = train_result.tvalues
    conf_int = train_result.conf_int()
    selected_var = pd.DataFrame([params, pvalues, bse, tvalues, conf_int[0], conf_int[1]])
    selected_var = selected_var.transpose()
    selected_var.columns = ['params', 'pvalues', 'bse', 'tvalues', 'conf_int0', 'conf_int1']
    selected_var['combine'] = selected_var.apply(lambda v: ','.join(str(x) for x in v.tolist()), axis=1)
    name = selected_var.index.tolist()
    combine = selected_var['combine'].tolist()
    var_train_result = zip(name, combine)
    data['selected_var'] = var_train_result

    all_var_list_with_target = [x + '_woe' for x in all_list]
    all_var_list_with_target.append(target)

    if withIntercept:
        all_var_list_with_target.append('intercept')

    train_x_with_target = train_woe[all_var_list_with_target]
    test_x_with_target = test_woe[all_var_list_with_target]

    model_para_list = train_result.params.index.tolist()

    marginal_var_train_result = get_marginal_var(train_x_with_target, test_x_with_target, target, model_para_list,
                                                 ks_group_num)

    if marginal_var_train_result is not None:
        marginal_var_train_result['combine'] = marginal_var_train_result[['KS_Train', 'KS_Test', 'P_Value']].apply(
            lambda v: ','.join(str(x) for x in v), axis=1)
        margin_name = marginal_var_train_result['var_name']
        margin_combine = marginal_var_train_result['combine'].tolist()
        margin_train_result = zip(margin_name, margin_combine)
        data['marginal_var'] = margin_train_result
    else:
        data['marginal_var'] = None

    model_ks = {}
    train_woe['prob_bad'] = train_result.predict(train_woe[woe_var_list])
    ks_train = ks_group(train_woe, target, 'prob_bad', ks_group_num, True).ks.max()

    test_woe['prob_bad'] = train_result.predict(test_woe[woe_var_list])
    ks_test = ks_group(test_woe, target, 'prob_bad', ks_group_num, True).ks.max()
    model_analysis['ks_train'] = ks_train
    model_analysis['ks_test'] = ks_test
    # data['model_ks'] = model_ks

    return data


def get_logit_backward(train_woe, test_woe, target, ks_group_num, in_vars=[], withIntercept=True, in_p_value=0.01,
                       in_max_loop=100):
    train_y = train_woe[[target]]
    woe_var_list = [x + '_woe' for x in in_vars]
    train_x = train_woe[woe_var_list]

    train_result = logit_backward(train_x, train_y, vars=woe_var_list, with_intercept=withIntercept, p_value=in_p_value,
                                  max_loop=in_max_loop)

    if train_result is None:
        return None

    data = {}

    model_analysis = {}
    model_analysis['target'] = target
    model_analysis['nobs'] = train_result.nobs
    model_analysis['df_model'] = str(train_result.model.df_model)
    model_analysis['df_resid'] = str(train_result.model.df_resid)
    model_analysis['prsquared'] = str(train_result.prsquared)
    model_analysis['aic'] = str(train_result.aic)
    model_analysis['bic'] = str(train_result.bic)
    model_analysis['likelyhood'] = str(train_result.llf)
    model_analysis['llnull'] = str(train_result.llnull)
    model_analysis['llr'] = str(train_result.llr_pvalue)
    data['model_analysis'] = model_analysis

    params = train_result.params
    pvalues = train_result.pvalues
    bse = train_result.bse
    tvalues = train_result.tvalues
    conf_int = train_result.conf_int()
    selected_var = pd.DataFrame([params, pvalues, bse, tvalues, conf_int[0], conf_int[1]])
    selected_var = selected_var.transpose()
    selected_var.columns = ['params', 'pvalues', 'bse', 'tvalues', 'conf_int0', 'conf_int1']
    selected_var['combine'] = selected_var.apply(lambda v: ','.join(str(x) for x in v.tolist()), axis=1)
    name = selected_var.index.tolist()
    combine = selected_var['combine'].tolist()
    var_train_result = zip(name, combine)

    data['selected_var'] = var_train_result

    if withIntercept:
        woe_var_list.append('intercept')
        train_woe['intercept'] = 1.0
        test_woe['intercept'] = 1.0

    woe_var_list_with_target = [x for x in woe_var_list]
    woe_var_list_with_target.append(target)
    train_x_with_target = train_woe[woe_var_list_with_target]
    test_x_with_target = test_woe[woe_var_list_with_target]


    woe_var_list.append(target)
    model_para_list = train_result.params.index.tolist()
    marginal_var_train_result = get_marginal_var(train_x_with_target, test_x_with_target, target, model_para_list,
                                                 ks_group_num)
    if marginal_var_train_result is not None:
        marginal_var_train_result['combine'] = marginal_var_train_result[['KS_Train', 'KS_Test', 'P_Value']].apply(
            lambda v: ','.join(str(x) for x in v), axis=1)
        margin_name = marginal_var_train_result['var_name']
        margin_combine = marginal_var_train_result['combine'].tolist()
        margin_train_result = zip(margin_name, margin_combine)
        data['marginal_var'] = margin_train_result
    else:
        data['marginal_var'] = None

    model_ks = {}
    ks_input_list = model_para_list


    train_woe['prob_bad'] = train_result.predict(train_woe[ks_input_list])
    ks_train = ks_group(train_woe, target, 'prob_bad', ks_group_num, True).ks.max()

    test_woe['prob_bad'] = train_result.predict(test_woe[ks_input_list])
    ks_test = ks_group(test_woe, target, 'prob_bad', ks_group_num, True).ks.max()
    model_analysis['ks_train'] = ks_train
    model_analysis['ks_test'] = ks_test
    data['model_ks'] = model_ks

    return data


def logit_backward(x, y, vars=[], with_intercept=True, p_value=0.05, max_loop=100):
    count = 0

    if (len(vars) <= 0):
        print '<ERROR>: no variable to select'
        return None

    X = x[vars].copy()
    next = True

    if with_intercept:
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
    if 'intercept' in aa.index: aa = aa.drop('intercept')
    aa.sort_values(inplace=True, ascending=True)
    aa = aa[aa < 0]
    print aa
    if len(aa) > 0:
        return aa.index[0]
    else:
        return None


def get_marginal_var(train_woe_data, test_woe_data, target, model_para_list, ks_group_num):
    marginal_var_result = marginal_selection(train_woe_data, test_woe_data, target, model_para_list, ks_group_num)
    return marginal_var_result


# remove high correlation variables
#
# def check_corr(x, y, corr_cap=0.75):
#     print 'checking corration'
#     base_col = set(x.columns)
#     corr = x.corr()
#     base_col = base_col.difference(set(['Intercept', 'intercept']))
#     base_col_list = list(base_col)
#     corr_pair = []
#     df_f = pd.DataFrame(y)
#     df_f.columns = ['target']
#     df = pd.concat([x, df_f], axis=1)
#     iv_dict = {}
#     for i in base_col_list:
#         uniqV = len(x[i].value_counts())
#         iv_dict[i] = getIV(df, i, 'target', 'num', uniqV)
#
#     base_col_list = list(base_col)
#     for i in range(len(base_col_list)):
#         for j in range(i + 1, len(base_col_list)):
#             if abs(corr[base_col_list[i]][base_col_list[j]] >= corr_cap):
#                 # compare IV
#                 if (iv_dict[base_col_list[i]] >= iv_dict[base_col_list[j]]):
#                     to_add = base_col_list[i]
#                     to_remove = base_col_list[j]
#                 else:
#                     to_add = base_col_list[j]
#                     to_remove = base_col_list[i]
#
#                 # choose big one into set and remove lower one
#                 base_col = base_col.difference(set([to_remove]))
#                 base_col = base_col.union(set([to_add]))
#     print 'Done with ckecking corration!'
#
#     return list(base_col)


    # df_try = pd.read_excel('/Users/xpbu/Documents/Work/maasFile/df_w_woe_all.xlsx')
    # df_try['intercept_woe'] = 1.0
    # target = 'bad_4w'
    # df_train_woe_try = df_try[df_try['dev_ind'] == 1]
    # df_test_woe_try = df_try[df_try['dev_ind'] == 0]
    #
    #
    # '''
    # all_list = [u'cell_operator',
    #             u'province',
    #             u'cell_loc',
    #             u'cell_operator_zh',
    #             u'信用评分_1',
    #             u'contacts_class1_cnt',
    #             u'phone_gray_score',
    #             u'芝麻信用',
    #             u'花呗金额',
    #             u'公司性质',
    #             u'call_in_cnt',
    #             u'居住情况',
    #             u'call_cnt',
    #             u'total_amount',
    #             u'学历',
    #             u'call_in_time',
    #             u'call_out_cnt',
    #             u'工作年限',
    #             u'手机入网时间',
    #             u'中高级职称']
    # '''
    # all_list = [u'cell_operator',
    #             u'province',
    #             u'cell_loc',
    #             u'信用评分_1',
    #             u'call_out_cnt',
    #             u'total_amount',
    #             u'学历',
    #             u'call_in_time']
    #
    # selected_list = [u'cell_operator',
    #                  u'cell_loc',
    #                  u'信用评分_1',
    #                  u'call_out_cnt']
    # ks_group_num = 20
    #
    # #result = get_logit_backward_manually(df_train_woe_try, df_test_woe_try, all_list, selected_list, target, ks_group_num)
    # result = get_logit_backward(df_train_woe_try, df_test_woe_try, target, ks_group_num, in_vars=all_list, withIntercept=True)
    # print result
