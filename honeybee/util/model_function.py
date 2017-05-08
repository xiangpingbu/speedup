# -*- coding: utf-8 -*-
"""
Created on Wed Mar 15 17:29:53 2017

@author: Admin
"""



from Model_Selection_Macro import *


# attributes selection
import json


def get_logit_backward(train, target, in_vars=[], in_varpatter='_woe', in_p_value=0.01, in_max_loop=100):

    train_y = train[[target]]
    woe_var_list = in_vars
    #woe_var_list = [x for x in train.columns if x.endswith(in_varpatter)]
    train_x = train[woe_var_list]

    result = logit_backward(train_x, train_y, vars=in_vars, p_value=in_p_value, max_loop=in_max_loop)
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
    selected_var['coef'] = result.params
    selected_var['pvalues'] = result.pvalues
    selected_var['bse'] = result.bse
    selected_var['z'] = result.tvalues
    selected_var['conf_int'] = result.conf_int()
    data['selected_var'] = selected_var


    #woe_var_list = [x for x in train_x.columns if x.endswith('woe')]
    woe_var_list.append(target)
    train_woe_data = train[woe_var_list]
    model_para_list = result.params.index.tolist()
    marginal_var_result = get_marginal_var(train_woe_data, target, model_para_list)
    data['marginal_var'] = marginal_var_result

    print data
    return data



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






def get_marginal_var(train_woe_data, target, model_para_list):

    marginal_var_result = Marginal_Selection(train_woe_data, target, model_para_list)
    return marginal_var_result


# train = pd.read_excel('/Users/xpbu/Documents/Work/maasFile/df_w_woe_all.xlsx')
target = 'bad_4w'
selected = [u'cell_operator_woe',
            u'province_woe',
            u'cell_loc_woe',
            u'cell_operator_zh_woe',
            u'信用评分_1_woe',
            u'contacts_class1_cnt_woe',
            u'phone_gray_score_woe',
            u'芝麻信用_woe',
            u'花呗金额_woe',
            u'公司性质_woe',
            u'call_in_cnt_woe',
            u'居住情况_woe',
            u'call_cnt_woe',
            u'total_amount_woe',
            u'学历_woe',
            u'call_in_time_woe',
            u'call_out_cnt_woe',
            u'工作年限_woe',
            u'手机入网时间_woe',
            u'net_flow_woe',
            u'公司规模_woe',
            u'性别_woe',
            u'代付工资月薪_woe',
            u'age_woe',
            u'宜信借款金额范围_woe',
            u'最近两月是否其他平台借款成功_woe',
            u'借款拼单状态_woe',
            u'宜信审批结果_woe',
            u'宜信状态（正常、逾期）_woe',
            u'年龄_woe',
            u'婚姻状况_woe',
            u'子女情况_woe',
            u'中高级职称_woe']
# result = get_logit_backward(train, target, in_vars=selected, in_p_value=0.05, in_max_loop=100)
#print result.params