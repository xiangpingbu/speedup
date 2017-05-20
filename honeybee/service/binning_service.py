"""
@author: xiangping
"""

import copy
import numpy as np
import pandas as pd
from sklearn.tree import DecisionTreeClassifier


def get_init_bin(df_train, target, invalid=None, min_leaf_rate=0.05):
    """
    :param df_train:
    :param target:
    :param invalid:
    :param min_leaf_rate: min_leaf_rate setting for get_tree_bin function
    :return: map <var, binning result>
            binning result: variable, variable type, tree_bin result, boundary list, IV
    """

    invalid_vars_list = invalid
    if invalid_vars_list is None:
        invalid_vars_list = []
    invalid_vars_list.append(target)

    # get initial binning
    df = df_train
    vars = df.columns
    df_iv = pd.DataFrame()
    iv_rank_map = {}

    for v in vars:
        if v not in invalid_vars_list:
            t = str(df[v].dtype)
            tree_bin = get_tree_bin(df, v, target, t, null_value_list=[], tree_deep=3, min_leaf_rate=min_leaf_rate)
            # IV
            var_iv = tree_bin['IV']
            if t in ['object', 'str']:
                boundary_list = str(tree_bin['df_map'][v].tolist())
            else:
                boundary_list = str(tree_bin['df_map']['min'].tolist())

            new_bin = pd.DataFrame({"var_name": v, "iv": var_iv, "tree_boundary": boundary_list}, index=["0"])
            df_iv = df_iv.append(new_bin)

            # boundary
            new_df_map = tree_bin['df_map']
            iv_rank_map[v] = (v, t, new_df_map, boundary_list, var_iv)

    return iv_rank_map


def get_tree_bin(df, var, target, var_type, null_value_list=[], tree_deep=3, min_leaf_rate=0.05):
    """
    porcess single variable
    :param df:
    :param var:
    :param target:
    :param var_type:
    :param null_value_list: user defined null value
    :param tree_deep:
    :param min_leaf_rate:
    :return: IV, my_type and tree_bin related table
    """

    min_leaf = int(len(df) * min_leaf_rate) + 1

    for nv in null_value_list:
        df[var] = df[var].replace(nv, np.nan)

    org_var = var
    my_type = 'Numerical'

    if var_type in ['object', 'str']:
        df = transform_categorical_to_woe(df, var, target)
        var += '_woe'
        my_type = 'Categorical'

    ss = decision_tree_bin(df, var, target, my_type, tree_deep=tree_deep, min_leaf=min_leaf)

    var += '_tree_bin'
    df_cur = ss['df_bin']
    df_cur[var] = df_cur['bad_hood_woe']
    df_cur['bad'] = df_cur[target]
    df_cur['good'] = 1 - df_cur[target]

    if my_type == 'Categorical':
        org_var_woe = org_var + '_woe'
        df_cur_grp = df_cur.groupby(var)
        df_mapping = pd.DataFrame(
            {'total': df_cur_grp.size(),
             org_var: df_cur_grp[org_var].unique(),
             'bads': df_cur_grp['bad'].sum(),
             'goods': df_cur_grp['good'].sum(),
             'bad_rate': (df_cur_grp['bad'].sum().astype(float) / df_cur_grp.size())
             }).reset_index()
        df_mapping['bad_rate'] = df_mapping['bad_rate'].apply('{0:.2%}'.format)
    else:
        df_cur_grp = df_cur.groupby(var)
        df_mapping = pd.DataFrame(
            {'total': df_cur_grp.size(),
             'max': df_cur_grp[org_var].max(),
             'min': df_cur_grp[org_var].min(),
             'bads': df_cur_grp['bad'].sum(),
             'goods': df_cur_grp['good'].sum(),
             'bad_rate': (df_cur_grp['bad'].sum().astype(float) / df_cur_grp.size())
             }).reset_index()
        df_mapping['bad_rate'] = df_mapping['bad_rate'].apply('{0:.2%}'.format)

    df_mapping['type'] = my_type
    df_mapping.rename(columns={var: 'woe'}, inplace=True)
    df_mapping['total_perc'] = (df_mapping.total / float(df_mapping.total.sum())).apply('{0:.2%}'.format)

    if my_type == 'Categorical':
        df_mapping.sort_values(['woe'], ascending=[1], inplace=True, na_position='first')
        df_mapping['bin_num'] = range(len(df_mapping))
        df_mapping = df_mapping[['bin_num', org_var, 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe', 'type']]
    else:
        df_mapping.sort_values(['min'], ascending=[1], inplace=True, na_position='first')
        if df_mapping['min'].isnull().values.any():
            df_mapping['bin_num'] = range(len(df_mapping))
        else:
            df_mapping['bin_num'] = [i + 1 for i in range(len(df_mapping))]
        df_mapping['max'] = df_mapping['max'].apply('{:.5f}'.format)
        df_mapping['min'] = df_mapping['min'].apply('{:.5f}'.format)
        df_mapping = df_mapping[
            ['bin_num', 'min', 'max', 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe', 'type']]

    df_mapping['index'] = range(len(df_mapping))
    df_mapping = df_mapping.set_index(['index'])

    return {'IV': sum(ss['df_woe'].IV), 'type': my_type, 'df_map': df_mapping}


def transform_categorical_to_woe(df, var, target, null_value_list=['NaNNaN']):
    """
    :param df: base dataframe
    :param var: categorical variables, single variable
    :param target:
    :param null_value_list:user defined null value
    :return: calculate the woe and append the woe back to the original df
    """

    df_temp = df[[var, target]].copy()
    df_woe = get_categorical_woe(df_temp, var, target, null_value_list=null_value_list)
    df_final = append_woe(df, var, df_woe)
    return df_final


def append_woe(df, var, df_woe, woe_var='woe', neutral_value=0, null_value_list=['NaNNaN']):
    """

    :param df: base dataframe
    :param var: categorical variables, single variable
    :param df_woe: woe related summary table
    :param woe_var: woe variable name
    :param neutral_value: set null woe value to neutral value
    :param null_value_list: user defined null value
    :return:
    """

    df_bs = df.copy()
    df_bs[var] = df_bs[var].replace(null_value_list, np.nan)
    df_dict = df_woe[[var, woe_var]]
    map_dict = {}
    for i in range(len(df_dict)):
        map_dict[df_dict.iloc[i][var]] = df_dict.iloc[i]['woe']

    new_var = var + '_woe'
    df_bs[new_var] = df_bs[[var]].applymap(map_dict.get)
    df_bs[new_var] = np.where(pd.isnull(df_bs[new_var]), neutral_value, df_bs[new_var])
    return df_bs


def get_categorical_woe(df, var, target, null_value_list=['NaNNaN']):
    """
    for given variable in dataframe, replace the nullValue
    binning the variable with all kinds of ways
    to replace category variable.

    assume: good is 0 and bad is 1

    :param df:
    :param var:
    :param target:
    :param null_value_list:
    :return: woe related summary table

    """

    delta_1 = 0.00001
    ddt = df[[var, target]].copy()
    ddt[var] = ddt[var].replace(null_value_list, np.nan)
    ddt['good'] = 1 - ddt[target]
    ddt['bad'] = ddt[target]

    grouped = ddt.groupby(var)
    agg_bad = pd.DataFrame([grouped.sum().bad.index, grouped.sum().bad]).transpose()
    agg_good = pd.DataFrame([grouped.sum().good.index, grouped.sum().good]).transpose()
    agg_bad.rename(columns={0: var, 1: 'bads'}, inplace=True)
    agg_good.rename(columns={0: var, 1: 'goods'}, inplace=True)

    agg = pd.merge(agg_bad, agg_good, on=var, how='outer')
    agg['bads'] = np.where(pd.isnull(agg.bads), 0, agg.bads)
    agg['goods'] = np.where(pd.isnull(agg.goods), 0, agg.goods)
    agg['total'] = agg.bads + agg.goods
    agg['odds'] = ((agg.bads + delta_1) / (agg.goods + delta_1))
    agg['oddsrt'] = (agg['odds'] / (float(agg.bads.sum()) / float(agg.goods.sum())))
    agg['woe'] = agg['oddsrt'].apply(lambda x: np.round(np.log(x) * 1.0, 4))
    agg['IV'] = (agg.bads / sum(agg.bads) - agg.goods / sum(agg.goods)) * agg.woe
    agg['bad_rate'] = (agg.bads / agg.total).apply('{0:.2%}'.format)
    agg.sort_values(['woe'], ascending=[1], inplace=True)
    agg['ks'] = ((agg.bads / ddt.bad.sum()).cumsum() - (agg.goods / ddt.good.sum()).cumsum())
    agg['total_perc'] = (agg.total / float(ddt.bad.sum() + ddt.good.sum())).apply('{0:.2%}'.format)

    del agg['oddsrt']

    return agg


def decision_tree_bin(df, var, target, var_type, tree_deep=3, min_leaf=200):
    """
    bin the variable using the decision tree algorithm
    :param df:
    :param var: always numerical value
    :param target:
    :param var_type: Categorical or Numerical
    :param tree_dep:
    :param min_leaf:
    :return: dict with 'df_woe': bin related summary table
                       'df_bin': whole df with woe value appended
    """
    # process single variable
    df_non_null = df[~pd.isnull(df[var])].copy()
    df_non_null["index"] = range(len(df_non_null))
    df_non_null = df_non_null.set_index(["index"])

    df_is_null = df[pd.isnull(df[var])].copy()
    df_is_null["index"] = range(len(df_is_null))
    df_is_null = df_is_null.set_index(["index"])

    # non-missing value go into the tree
    dt = DecisionTreeClassifier(criterion='entropy', max_depth=tree_deep, min_samples_leaf=min_leaf)
    df_non_null_tree = dt.fit(df_non_null[[var]], df_non_null[target])

    df_non_null_predict = pd.DataFrame(df_non_null_tree.predict_proba(df_non_null[[var]]))
    df_non_null_predict.rename(columns={0: 'good_hood', 1: 'bad_hood'}, inplace=True)

    if var_type in ['object', 'str', 'Categorical']:
        # categorical variable should not use _woe in the name
        org_var = var.replace('_woe', '')
        df_non_null_predict = pd.concat([df_non_null_predict[['bad_hood']], df_non_null[[target, var, org_var]]],
                                        axis=1)
    else:
        df_non_null_predict = pd.concat([df_non_null_predict[['bad_hood']], df_non_null[[target, var]]], axis=1)

    # missing value need to be process separately
    if len(df_is_null) > 1:
        df_is_null['good'] = 1 - df_is_null[target]
        df_is_null['bad'] = df_is_null[target]

        null_bad_hood = float(df_is_null.bad.sum()) / len(df_is_null)
        df_is_null_predict = df_is_null[[target]]
        df_is_null_predict['bad_hood'] = null_bad_hood
        df_is_null_predict[var] = np.nan

        df_predict = df_non_null_predict.append(df_is_null_predict)
        df_predict["index"] = range(len(df_predict))
        df_predict = df_predict.set_index(["index"])
    else:
        df_predict = df_non_null_predict

    # add bad_rate
    df_woe = get_categorical_woe(df_predict, 'bad_hood', target)
    df_result = transform_categorical_to_woe(df_predict, 'bad_hood', target)

    return {'df_woe': df_woe, 'df_bin': df_result}

def bin_assign_categorical(bin_list, v):
    for index, bin in enumerate(bin_list):
        # null value assign -1 bin number
        if str(v) in bin:
            return index
    # should never reach this line
    return -1


def get_min_boundary_mapping_reverse(min_boundary_list, pre=0):
    max_min_boundary = []
    # pre = np.nan

    for index, mb in enumerate(min_boundary_list):
        if index < len(min_boundary_list) - 1:
            max_min_boundary.append((min_boundary_list[index], min_boundary_list[index + 1]))
    max_min_boundary.append((pre, np.nan))

    #    max inclusive, min exclusive
    return dict(zip(range(0, len(max_min_boundary)), max_min_boundary))


def bin_reverse_assign_max(bin_map_reserve, v):
    ret = bin_map_reserve.get(v)
    if ret is None:
        return np.nan
    else:
        return ret[1]


def bin_reverse_assign_min(bin_map_reserve, v):
    ret = bin_map_reserve.get(v)
    if ret is None:
        return np.nan
    else:
        return ret[0]


def get_single_variable_bin(df_train, var_type, my_var, my_target, boundary_list):
    '''

    :param df_train:
    :param type:
    :param my_var:
    :param my_target:
    :param boundary_list:
    :return: a table with binning result and woe
    '''

    # if var_type is False:
    #     var_type = 'Numerical'
    # else:
    #     var_type = 'Categorical'

    if var_type == 'Numerical':
        bin_result = get_single_variable_bin_numeric(df_train, my_var, my_target, boundary_list)
    else:
        bin_result = get_single_variable_bin_categorical(df_train, my_var, my_target, boundary_list)

    return bin_result


def get_single_variable_bin_numeric(df, var, target, boundary_list):
    """

    :param df:
    :param var:
    :param target:
    :param boundary_list: user defined boundary list
    :return: single_variable_bin_numeric manually
    """

    # process single variable
    boundary_list_with_nan = copy.copy(boundary_list)
    # sort the boundary_list
    if not np.isnan(boundary_list).any():
        boundary_list_with_nan.append(np.nan)

    # [15,5,2, nan]
    boundary_list_high_to_low_sort = sorted(boundary_list_with_nan, cmp_with_nan_reverse)

    # [nan, 1,3,5] or [1,3,5]
    boundary_list_low_to_high_sort = sorted(boundary_list, cmp_with_nan)
    df_cur = df[[var, target]].copy()
    bin_map = get_boundary_mapping(boundary_list_high_to_low_sort)
    # generate the bin_num for grouping
    df_cur['bin_num'] = df_cur[var].apply(lambda x: bin_assign(bin_map, x))
    df_woe = get_single_var_bin_woe_numerical(df_cur, var, 'bin_num', target, boundary_list_low_to_high_sort)
    df_woe['type'] = 'Numerical'
    #    df_woe.sort(['bin_num'], ascending=[1], inplace=True)

    return df_woe


def get_single_variable_bin_categorical(df, var, target, bin_list):
    """
    process single variable
    :param df:
    :param var:
    :param target:
    :param bin_list:
    :return: woe information according to the bin_list
    """
    df_cur = df[[var, target]].copy()
    df_cur['bin_num'] = df_cur[var].apply(lambda x: bin_assign_categorical(bin_list, x))
    df_woe = get_single_var_bin_woe_categorical(df_cur, 'bin_num', target)
    df_woe[var] = df_woe['bin_num'].apply(lambda x: bin_reverse_assign_categorical(bin_list, x))
    df_woe['type'] = 'Categorical'
    df_woe['bin_num'] = range(0, len(df_woe))

    return df_woe


def get_single_var_bin_woe_numerical(df, var_ori, var_bin, target, boundary_list):
    """
    assume: good is 0 and bad is 1
    :param df:
    :param var_ori:
    :param var_bin:
    :param target:
    :param boundary_list: user defined boundary list
    :return: single variable woe calculation according with the var_bin
    """

    delta_1 = 0.00001
    ddt = df[[var_ori, var_bin, target]].copy()
    ddt['good'] = 1 - ddt[target]
    ddt['bad'] = ddt[target]
    ddt['var_ori'] = ddt[var_ori]

    grouped = ddt.groupby(var_bin)
    agg_bad = pd.DataFrame([grouped.sum().bad.index, grouped.sum().bad]).transpose()
    agg_good = pd.DataFrame([grouped.sum().good.index, grouped.sum().good]).transpose()
    agg_bad.rename(columns={0: var_bin, 1: 'bads'}, inplace=True)
    agg_good.rename(columns={0: var_bin, 1: 'goods'}, inplace=True)

    agg_min_max = pd.DataFrame([grouped.sum().var_ori.index, grouped.min().var_ori, grouped.max().var_ori]).transpose()
    agg_min_max.rename(columns={0: var_bin, 1: 'min', 2: 'max'}, inplace=True)

    agg = pd.merge(agg_bad, agg_good, on=var_bin, how='outer')
    agg = pd.merge(agg, agg_min_max, on=var_bin, how='outer')
    agg['bads'] = np.where(pd.isnull(agg.bads), 0, agg.bads)
    agg['goods'] = np.where(pd.isnull(agg.goods), 0, agg.goods)
    agg['total'] = agg.bads + agg.goods

    agg['odds'] = ((agg.bads + delta_1) / (agg.goods + delta_1))
    agg['oddsrt'] = (agg['odds'] / (float(agg.bads.sum()) / float(agg.goods.sum())))
    agg['woe'] = agg['oddsrt'].apply(lambda x: np.round(np.log(x) * 1.0, 4))
    agg['IV'] = (agg.bads / sum(agg.bads) - agg.goods / sum(agg.goods)) * agg.woe

    agg['bad_rate'] = (agg.bads / agg.total).apply('{0:.2%}'.format)

    agg.sort_values(['bin_num'], ascending=[1], inplace=True)
    # agg['ks'] = ((agg.bads / ddt.bad.sum()).cumsum() - (agg.goods / ddt.good.sum()).cumsum())
    agg['total_perc'] = (agg.total / float(ddt.bad.sum() + ddt.good.sum())).apply('{0:.2%}'.format)
    agg['min_boundary'] = boundary_list

    min_shift = agg['min'][1:].append(pd.Series(np.inf))
    min_shift = min_shift.reset_index()
    del min_shift['index']
    agg['max_boundary'] = min_shift
    agg['max_boundary'] = agg['max_boundary']
    del agg['oddsrt']

    return agg


def get_single_var_bin_woe_categorical(df, var, target, null_value=['NaNNaN']):
    """
    for given variable in dataframe, replace the nullValue and binning the variable with all kinds of ways
    to replace category variable.
    assume: good is 0 and bad is 1
    :param df:
    :param var:
    :param target:
    :param null_value:
    :return:
    """

    delta_1 = 0.00001
    ddt = df[[var, target]].copy()
    ddt[var] = ddt[var].replace(null_value, np.nan)
    ddt['good'] = 1 - ddt[target]
    ddt['bad'] = ddt[target]

    grouped = ddt.groupby(var)
    agg_bad = pd.DataFrame([grouped.sum().bad.index, grouped.sum().bad]).transpose()
    agg_good = pd.DataFrame([grouped.sum().good.index, grouped.sum().good]).transpose()
    agg_bad.rename(columns={0: var, 1: 'bads'}, inplace=True)
    agg_good.rename(columns={0: var, 1: 'goods'}, inplace=True)

    agg = pd.merge(agg_bad, agg_good, on=var, how='outer')
    agg['bads'] = np.where(pd.isnull(agg.bads), 0, agg.bads)
    agg['goods'] = np.where(pd.isnull(agg.goods), 0, agg.goods)
    agg['total'] = agg.bads + agg.goods
    agg['odds'] = ((agg.bads + delta_1) / (agg.goods + delta_1))
    agg['oddsrt'] = (agg['odds'] / (float(agg.bads.sum()) / float(agg.goods.sum())))
    agg['woe'] = agg['oddsrt'].apply(lambda x: np.round(np.log(x) * 1.0, 4))
    agg['IV'] = (agg.bads / sum(agg.bads) - agg.goods / sum(agg.goods)) * agg.woe

    agg['bad_rate'] = (agg.bads / agg.total).apply('{0:.2%}'.format)

    agg.sort_values(['woe'], ascending=[1], inplace=True)
    # agg['ks'] = ((agg.bads / ddt.bad.sum()).cumsum() - (agg.goods / ddt.good.sum()).cumsum())
    agg['total_perc'] = (agg.total / float(ddt.bad.sum() + ddt.good.sum())).apply('{0:.2%}'.format)

    del agg['oddsrt']

    return agg


def adjust_bin(df, type_bool, var_name, boundary_list, target=None, expected_column=None):
    """
    :param df:
    :param var_type:
    :param var_name:
    :param boundary_list:
    :param target:
    :param expected_column: variable set
    :return: adjust just the binning according to the boundary_list
    """
    expected_column.add(target)
    my_df = pd.DataFrame(df, columns=expected_column)
    var_type = 'Categorical'
    if type_bool is False:
        var_type = 'Numerical'

    result = get_single_variable_bin(my_df, var_type, var_name, target, boundary_list)
    return result


def cmp_with_nan_reverse(x, y):
    if str(x) == 'nan':
        return 1
    if str(y) == 'nan':
        return -1
    if x < y:
        return 1
    if x > y:
        return -1
    return 0


def cmp_with_nan(x, y):
    if str(x) == 'nan':
        return -1
    if str(y) == 'nan':
        return 1
    if x < y:
        return -1
    if x > y:
        return 1
    return 0


def get_boundary_mapping(boundary_list):
    # as a list to keep the order: high to low, nan as the last
    return zip(boundary_list, range(0, len(boundary_list))[::-1])


def get_boundary_mapping_reverse(max_boundary_list, pre=0):
    max_min_boundary = []
    # pre = np.nan
    for mb in max_boundary_list:
        max_min_boundary.append((pre, mb))
        pre = mb
    max_min_boundary.append((pre, np.nan))

    #    max inclusive, min exclusive
    return dict(zip(range(0, len(max_min_boundary)), max_min_boundary))


def bin_assign(bin_map, v):
    # sort my order to high to low accroding to the key
    for key, value in bin_map:
        if v >= key:
            return value
    return 0


def get_manual_bin_numeric(df, var, target, boundary_list, pre):
    # process single variable
    # sort the boundary_list
    print boundary_list
    bl = sorted(boundary_list, cmp)
    df_cur = df[[var, target]].copy()

    bin_map = get_boundary_mapping(bl)
    print bin_map
    bin_map_reverse = get_boundary_mapping_reverse(bl, pre)
    df_cur['bin_num'] = df_cur[var].apply(lambda x: bin_assign(bin_map, x))
    df_woe = get_categorical_woe(df_cur, 'bin_num', target)
    df_woe['min'] = df_woe['bin_num'].apply(lambda x: bin_reverse_assign_min(bin_map_reverse, x)).apply(
        '{:.15f}'.format)
    df_woe['max'] = df_woe['bin_num'].apply(lambda x: bin_reverse_assign_max(bin_map_reverse, x)).apply(
        '{:.15f}'.format)
    df_woe['category_t'] = 'False'

    df_result = df_cur
    df_result['min'] = df_cur['bin_num'].apply(lambda x: bin_reverse_assign_min(bin_map_reverse, x))
    df_result['max'] = df_cur['bin_num'].apply(lambda x: bin_reverse_assign_max(bin_map_reverse, x))
    df_result['category_t'] = 'False'
    df_final = append_woe(df_result, 'bin_num', df_woe, woe_var='woe')
    df_final[var + '_woe'] = df_final['bin_num_woe']
    del df_final['bin_num_woe']

    df_woe.sort(['bin_num'], ascending=[1], inplace=True)

    return {'df_woe': df_woe, 'df_result': df_final}


def bin_reverse_assign_categorical(bin_list, x):
    return bin_list[x]


def get_manual_bin_categorical(df, var, target, bin_list):
    # process single variable
    df_cur = df[[var, target]].copy()
    df_cur['bin_num'] = df_cur[var].apply(lambda x: bin_assign_categorical(bin_list, x))
    df_woe = get_categorical_woe(df_cur, 'bin_num', target)
    df_woe[var] = df_woe['bin_num'].apply(lambda x: bin_reverse_assign_categorical(bin_list, x))
    df_woe['category_t'] = 'True'

    df_result = df_cur
    df_result['category_t'] = 'True'
    df_woe.sort(['bin_num'], ascending=[1], inplace=True)
    df_final = append_woe(df_result, 'bin_num', df_woe, woe_var='woe')
    df_final[var + '_woe'] = df_final['bin_num_woe']
    del df_final['bin_num_woe']

    return {'df_woe': df_woe, 'df_result': df_final}
