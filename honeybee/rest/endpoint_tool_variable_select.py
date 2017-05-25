# coding=utf-8
from rest.app_base import *
from service.db import tool_model_service
from service import db_service as vs
from service import logit_model_service as lmf
import pandas as pd
from util import common



base = '/tool'


@app.route(base + "/variable_select", methods=['POST'])
def variable_select():
    """
    apply完成后,第一次进入时的变量选择
    """
    model_name = request.form.get("modelName")
    branch = request.form.get("branch")
    var_list = request.form.get("var_list")

    df_map = global_value.get_value(model_name + "_" + branch)

    # 调用接口时发现var_list为空,那么主动从数据库中读取
    if var_list is None or var_list == '':
        result = vs.get_selected_variable(model_name, branch)[0]
        var_list = result["selected_variable"].decode('utf-8')
    else:
        # 清除旧数据,插入新的数据
        if (vs.del_selected_variable(model_name, branch)):
            vs.save_selected_variable(model_name, branch, var_list)
        else:
            return responseto(messege="fail to save selected variable", success=False)
    target = request.form.get("target")
    withIntercept = request.form.get("with_intercept") == 'true'
    ks_group_num = request.form.get("ks_group_num")
    ks_group_num = ks_group_num if ks_group_num != '' else 20

    df_train_woe = df_map["df_train_woe"]
    df_test_woe = df_map["df_test_woe"]

    data = lmf.get_logit_backward(df_train_woe, df_test_woe, target, ks_group_num, var_list.split(","),
                                  withIntercept)
    if data is None:
        return responseto(success=False)
    return responseto(data=data)



@app.route(base + "/variable_select_manual", methods=['POST'])
def variable_select_manual():
    """
    手动选择变量
    """
    all_list = request.form.get("all_list")
    selected_list = request.form.get("selected_list")
    target = request.form.get("target")
    with_intercept = request.form.get("with_intercept") == 'true'
    model_name = request.form.get("modelName")
    branch = request.form.get("branch")
    ks_group_num = request.form.get("ks_group_num")

    df_map = global_value.get_value(model_name + "_" + branch)
    df_train_woe = df_map["df_train_woe"]
    df_test_woe = df_map["df_test_woe"]

    ks_group_num = ks_group_num if ks_group_num != '' else 20

    data = lmf.get_logit_backward_manually(df_train_woe, df_test_woe, all_list.split(","),
                                           selected_list.split(","), target, ks_group_num, with_intercept)
    return responseto(data=data)

@app.route(base+"/variable_verify",methods=['POST'])
def variable_verify():
    """
    变量相关性校验
    """
    model_name = request.form.get("modelName")
    branch = request.form.get("branch")
    corr_cap = request.form.get("corrCap")
    variables = request.form.get("variables")

    variable_list = variables.split(",")
    df_map = global_value.get_value(model_name+"_"+branch)

    df_train = df_map["df_train"]

    df = pd.DataFrame(df_train,
                      columns=variable_list)

    if common.get_iv_tree_binning(df,corr_cap) is False:
        result = common.get_correlation(df)
        return responseto(result.to_dict())
    else:
        return responseto(None)
