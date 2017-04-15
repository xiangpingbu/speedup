# -*- coding: utf-8 -*-
"""
Created on Wed Mar 15 11:22:37 2017

@author: Admin
"""

import pandas as pd
import numpy as np
from Binning_Function import *
# execfile('Binning_Function.py')


def adjust(excel_obj,type,var_name,boundary_list):
    ######################## read data ########################
    # df0 = pd.read_excel(file)

    target = 'bad_7mon_60'

    invalid_vars_list = [target, 'apply_id','client_birthday', 'loan_loan_periods_max', \
                         'loan_loan_overdue_month_over_avg','loan_loan_periods_min', \
                         'personal_live_join_other','job_comp_join_date','loan_overdue_periods_max', \
                         'client_birthday','loan_loan_provide_date_min', \
                         'credit_c_last_repayment_date_max','job_comp_join_date','loan_last_repayment_loan_date_max', \
                         'loan_loan_status_endmonth_max','app_agree_time_limit']
    #                         'credit_sum_one_month_overdue_loan']

    df1 = pd.DataFrame(excel_obj,columns = {target, 'credit_query_times_three', 'personal_live_join', 'credit_c_utilization'})
    df = df1.head(4000)

    ########################## dev vs val ##############################
    from sklearn.cross_validation import train_test_split
    target = df[['bad_7mon_60']]
    # df_train, df_test, y_train, y_test= train_test_split(df, target,test_size = 1,random_state = 40, stratify=target)


    my_target = 'bad_7mon_60'

    # my_var = 'personal_live_join'
    # my_boundary_list = [np.array([u'4,', u'3,'], dtype=object), np.array([u'2,', u'1,2,', u'1,', u'2,4,', u'1,4,', u'1,2,4,'], dtype=object)]
    # [df_train, df_test] = single_categorical(df_train, df_test, my_var, my_target, my_boundary_list)


    # my_boundary_list =  [0.24981299212598429, 0.59972652468538235, 0.73281381634372367]
    # my_boundary_list =  [ 0.4013125,0.622375,0.8539396551724138,1.668584905660377,"nan"]
    # my_boundary_list =  []
    [result,result_all] = single_numerical_no_html(df1, type, var_name, my_target, boundary_list)
    return [result,result_all]
#
# adjust("df_train.xlsx",'credit_c_utilization',[ 0.4013125,0.622375,0.8539396551724138,1.668584905660377,"nan"])