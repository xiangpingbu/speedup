# -*- coding: utf-8 -*-
"""
@author: xiangping
"""

import random

import numpy as np
import pandas as pd
import statsmodels.api as sm

from service import ks_calculation_service as kf


def logisticReg_KS(df_train, df_test, target, para_list, ks_group_num):
    """
    :param df_train:
    :param df_test:
    :param target:
    :param para_list: selected variable list
    :param ks_group_num: number of group used in ks calculation
    :return:
    """
    result_temp = logit_base_model(df_train[para_list], df_train[target])

    if result_temp is None:
        return [None, None]
    p = result_temp.summary2()
    p_value = p.tables[1][u'P>|z|'][-1]
    #    get score for training set
    df_train['prob_bad'] = result_temp.predict(df_train[para_list])
    df_test['prob_bad'] = result_temp.predict(df_test[para_list])

    #    calculate KS
    ks_train = kf.ks_group_equal(df_train, target, 'prob_bad', ks_group_num, True).ks.max()
    ks_test = kf.ks_group_equal(df_test, target, 'prob_bad', ks_group_num, True).ks.max()

    return [ks_train, ks_test, p_value]


def marginal_selection(df_train, df_test, target, model_para_list, ks_group_num):
    """
    :param df_train:
    :param df_test:
    :param target:
    :param model_para_list:
    :param ks_group_num:
    :return: show the KS and P_value for the non-selected variable in the backward
    """
    all_para_list = df_train.columns.tolist()
    step2_list = [x for x in all_para_list if x not in model_para_list and x not in [target, 'apply_id']]
    if len(step2_list) == 0:
        return None
    result = pd.DataFrame()
    for var in step2_list:
        model_para_list_add = model_para_list + [var]
        print "current adding var is: ", var
        [ks_train, ks_test, p_value] = logisticReg_KS(df_train, df_test, target, model_para_list_add, ks_group_num)
        result_temp = pd.DataFrame({"var_name": var, "KS_Train": ks_train, "KS_Test": ks_test, "P_Value": p_value},
                                   index=["0"])
        result = result.append(result_temp)
    result.sort_values('KS_Train', ascending=False, inplace=True)
    return result


def logit_base_model(x, y, try_cnt=1):
    """
    :param x:
    :param y:
    :param try_cnt:
    :return: try logit model, deal with singular Matrix exception
    """
    print '<MDL start>'
    print 'try count: ', try_cnt
    if try_cnt > 10:
        print 'exceed 10 tries, stop !'
        return None

    try:
        logit = sm.Logit(y, x)
        result = logit.fit()
        return result
    except np.linalg.LinAlgError, e:
        print "error in variable selection: ", e
        s_x = variable_order_shuffle(x)
        return logit_base_model(s_x, y, try_cnt + 1)
    except Exception, e:
        print e
        return None


def variable_order_shuffle(df):
    """

    :param df:
    :return: random shuffle the columns of df
    """

    c_list = df.columns.tolist()
    random.shuffle(c_list)
    new_df = df[c_list]
    return new_df


def get_logit_backward_manually(train_woe, test_woe, all_list, selected_list, target, ks_group_num=20,
                                withIntercept=True):
    """

    :param train_woe:
    :param test_woe:
    :param all_list:
    :param selected_list:
    :param target:
    :param ks_group_num:
    :param withIntercept:
    :return: run the logit backward function manually, and return the result
    three part:
    1. model analysis
    2. model detailed information
    3. variable analysis for the non-selected varaibles
    """
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
    ks_train = kf.ks_group_equal(train_woe, target, 'prob_bad', ks_group_num, True).ks.max()

    test_woe['prob_bad'] = train_result.predict(test_woe[woe_var_list])
    ks_test = kf.ks_group_equal(test_woe, target, 'prob_bad', ks_group_num, True).ks.max()
    model_analysis['ks_train'] = ks_train
    model_analysis['ks_test'] = ks_test
    # data['model_ks'] = model_ks

    return data


def get_logit_backward(train_woe, test_woe, target, ks_group_num, in_vars=[], withIntercept=True, in_p_value=0.01,
                       in_max_loop=100):
    """

    :param train_woe:
    :param test_woe:
    :param target:
    :param ks_group_num:
    :param in_vars:
    :param withIntercept:
    :param in_p_value:
    :param in_max_loop:
    :return:  run the logit backward function initially, and return the result
    three part:
    1. model analysis
    2. model detailed information
    3. variable analysis for the non-selected variables
    """
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
    ks_train = kf.ks_group_equal(train_woe, target, 'prob_bad', ks_group_num, True).ks.max()

    test_woe['prob_bad'] = train_result.predict(test_woe[ks_input_list])
    ks_test = kf.ks_group_equal(test_woe, target, 'prob_bad', ks_group_num, True).ks.max()
    model_analysis['ks_train'] = ks_train
    model_analysis['ks_test'] = ks_test
    data['model_ks'] = model_ks

    return data


def logit_backward(x, y, vars=[], with_intercept=True, p_value=0.05, max_loop=100):
    """

    :param x:
    :param y:
    :param vars:
    :param with_intercept:
    :param p_value:
    :param max_loop:
    :return: logit_backward process
    """
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
        pv.rename(columns={'index': 'var_name', 0: 'p_value'}, inplace=True)
        temp_dict = pv.ix[pv['p_value'].idxmax()]
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
    """

    :param mdlresult:
    :return:
    """
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
    """
    :param train_woe_data:
    :param test_woe_data:
    :param target:
    :param model_para_list:
    :param ks_group_num:
    :return: analysis for the non-selected variables
    """

    marginal_var_result = marginal_selection(train_woe_data, test_woe_data, target, model_para_list, ks_group_num)
    return marginal_var_result
