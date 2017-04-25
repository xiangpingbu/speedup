import pandas as pd
import numpy as np
from sklearn.tree import DecisionTreeClassifier
from sklearn.externals.six import StringIO
import pydotplus
from sklearn import tree
# from pylab import *



def transform_categorical_to_woe(df, var, target, null_value='NaNNaN'):
    '''
    treat var as categorical variables
    calculate the woe and append the woe back to the original df
    return the new df
    '''

    df_temp = df[[var, target]].copy()
    #df_temp[var] = df_temp[var].astype('str')
    df_woe = get_categorical_woe(df_temp, var, target, null_value=null_value)

    df_final = append_woe(df, var, df_woe)
    return df_final


def append_woe(df, var, df_woe, woe_var='woe', neutral_value=0, null_value=['NaNNaN']):
    '''
    for null woe value, using neutral_value (???)
    '''

    df_bs = df.copy()
    # is a bug in zoe code?
    #df_bs[var] = df_bs[var].replace(np.nan, null_value)
    df_bs[var] = df_bs[var].replace(null_value, np.nan)
    df_dict = df_woe[[var, woe_var]]

    map_dict = {}
    for i in range(len(df_dict)):
        map_dict[df_dict.iloc[i][var]] = df_dict.iloc[i]['woe']

    new_var = var + '_woe'
    df_bs[new_var] = df_bs[[var]].applymap(map_dict.get)
    df_bs[new_var] = np.where(pd.isnull(df_bs[new_var]), neutral_value, df_bs[new_var])
    return df_bs


def get_categorical_woe(df, var, target, null_value=['NaNNaN']):
    """
    for given variable in dataframe, replace the nullValue and binning the variable with all kinds of ways
    to replace category variable.
    assume: good is 0 and bad is 1
    """

    delta_1 = 0.00001
    ddt = df[[var, target]].copy()
    # is a bug? in zoe code?
    #ddt[var] = ddt[var].replace(np.nan, null_value)
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

    agg['odds'] = ((agg.goods + delta_1) / (agg.bads + delta_1))
    agg['oddsrt'] = (agg['odds'] / (float(agg.goods.sum()) / float(agg.bads.sum())))
    agg['woe'] = agg['oddsrt'].apply(lambda x: np.round(np.log(x) * 1.0, 4))
    agg['IV'] = (agg.goods / sum(agg.goods) - agg.bads / sum(agg.bads)) * agg.woe

    agg['bad_rate'] = (agg.bads / agg.total).apply('{0:.2%}'.format)
    #agg['min'] = agg['min'].apply('{:.15f}'.format)
    #agg['max'] = agg['max'].apply('{:.15f}'.format)

    agg.sort_values(['woe'], ascending=[1], inplace=True)
    agg['ks'] = ((agg.bads / ddt.bad.sum()).cumsum() - (agg.goods / ddt.good.sum()).cumsum())
    agg['total_perc'] = (agg.total / float(ddt.bad.sum() + ddt.good.sum())).apply('{0:.2%}'.format)

    del agg['oddsrt']

    return agg


def get_basic_info(df):
    df_name = pd.DataFrame(list(df.columns))
    df_name.rename(columns={0: 'orig_name'}, inplace=True)

    df_dtypes = pd.DataFrame(df.dtypes)
    df_dtypes.reset_index(inplace=True)
    df_dtypes.rename(columns={'index': 'orig_name', 0: 'd_type'}, inplace=True)
    df_name = pd.merge(df_name, df_dtypes, on='orig_name', how='inner')
    df_name['coverage'] = 0
    df_name['uniqueVal'] = 0
    for i in range(len(df_name)):
        col = str(df_name.iloc[i]['orig_name'])
        df_name.ix[i, 'coverage'] = get_col_coverage(df, col)
        df_name.ix[i, 'uniqueVal'] = get_unique_value_count(df, col)

    df_name['uniqueVal'] = df_name['uniqueVal'].astype('int')
    df_name['orig_name'] = df_name['orig_name'].astype('str')
    df_name['d_type'] = df_name['d_type'].astype('str')
    df_name['coverage'] = df_name['coverage'].astype('float')
    return df_name


def get_col_coverage(df, col, null_value=[]):
    df_tmp = df[[col]].copy()
    tot_len = len(df_tmp)
    if len(null_value) > 0:
        df_tmp[col] = np.where(df_tmp[col].isin(null_value), np.nan, df_tmp[col])

    valid_len = len(df[~pd.isnull(df[col])])
    return valid_len * 1.0 / tot_len


def get_unique_value_count(df, col):
    df_tmp = df[[col]].copy()
    tt = len(df_tmp[col].value_counts())
    return tt


def df_unique_value_filter(df, unique_value_upper):
    df = df[((df.uniqueVal < unique_value_upper) & (df.d_type.isin(['object', 'str']))) | (
        'object' != df.d_type)].copy().reset_index().copy()
    del df['index']
    return df


def decision_tree_bin(df, var, target, varType, tree_dep=3, min_leaf=200):

    #process single variable
    df_non_null = df[~pd.isnull(df[var])].copy()
    df_non_null["index"] = range(len(df_non_null))
    df_non_null = df_non_null.set_index(["index"])

    df_is_null = df[pd.isnull(df[var])].copy()
    df_is_null["index"] = range(len(df_is_null))
    df_is_null = df_is_null.set_index(["index"])

    #non-missing value go into the tree
    dt = DecisionTreeClassifier(criterion='entropy', max_depth=tree_dep, min_samples_leaf=min_leaf)
    df_non_null_tree = dt.fit(df_non_null[[var]], df_non_null[target])


    df_non_null_predict = pd.DataFrame(df_non_null_tree.predict_proba(df_non_null[[var]]))
    df_non_null_predict.rename(columns={0: 'good_hood', 1: 'bad_hood'}, inplace=True)

    if varType in ['object', 'str']:
        #categorical variable should not use _woe in the name
        org_var = var.replace('_woe', '')
        df_non_null_predict = pd.concat([df_non_null_predict[['bad_hood']], df_non_null[[target, var, org_var]]], axis=1)
    else:
        df_non_null_predict = pd.concat([df_non_null_predict[['bad_hood']], df_non_null[[target, var]]], axis=1)
    # df_non_null_predict schema: bad_hood  bad_6mon_60  credit_total_loan_money

    # missing value need to be process separately
    if len(df_is_null) > 1:
        df_is_null['good'] = 1 - df_is_null[target]
        df_is_null['bad'] = df_is_null[target]

        null_bad_hood = float(df_is_null.bad.sum())/len(df_is_null)
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
    df_woe['min'] = df_woe['bin_num'].apply(lambda x: bin_reverse_assign_min(bin_map_reverse, x)).apply('{:.15f}'.format)
    df_woe['max'] = df_woe['bin_num'].apply(lambda x: bin_reverse_assign_max(bin_map_reverse, x)).apply('{:.15f}'.format)
    df_woe['category_t'] = 'False'

    df_result = df_cur
    df_result['min'] = df_cur['bin_num'].apply(lambda x: bin_reverse_assign_min(bin_map_reverse, x))
    df_result['max'] = df_cur['bin_num'].apply(lambda x: bin_reverse_assign_max(bin_map_reverse, x))
    df_result['category_t'] = 'False'
    df_final = append_woe(df_result, 'bin_num', df_woe, woe_var='woe')
    df_final[var+'_woe'] = df_final['bin_num_woe']
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
    df_final[var+'_woe'] = df_final['bin_num_woe']
    del df_final['bin_num_woe']

    return {'df_woe': df_woe, 'df_result': df_final}


def bin_assign_categorical(bin_list, v):
    for index, bin in enumerate(bin_list):
        # null value assign -1 bin number
        if str(v) in bin:
            return index
    return -1



def get_min_boundary_mapping_reverse(min_boundary_list,pre=0):
    max_min_boundary = []
    # pre = np.nan

    for index,mb in enumerate(min_boundary_list):
        if index < len(min_boundary_list)-1:
            max_min_boundary.append((min_boundary_list[index], min_boundary_list[index+1]))
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


'''
def generate_table_item_html(s, html_file, var_name, type):
    if type == 'object':
        html_file.write('<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>' % (s['bin_num'], s[var_name], s['total'], s['total_perc'], s['woe'], s['category_t']))
    else:
        content = '<tr>'+'<td>'+str(s['bin_num'])+'</td>'+'<td>'+str(s['min'])+'</td>'+'<td>'+str(s['max'])+'</td>'+'<td>'+str(s['total'])+'</td>'+'<td>'+str(s['total_perc'])+'</td>'+'<td>'+str(s['woe'])+'</td>'+'<td>'+str(s['category_t'])+'</td>'+'</tr>'
        html_file.write(content)


def generate_table_item_html_single(s, html_file, var_name, type):
    if type == 'object':
        html_file.write('<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>' % (s['bin_num'], s[var_name], s['total'], s['total_perc'], s['woe'], s['category_t']))
    else:
        content = '<tr>'+'<td>'+str(s['bin_num'])+'</td>'+'<td>'+str(s['min'])+'</td>'+'<td>'+str(s['max'])+'</td>'+'<td>'+str(s['bads'])+'</td>'+'<td>'+str(s['goods'])+'</td>'+'<td>'+str(s['total'])+'</td>'+'<td>'+str(s['total_perc'])+'</td>'+'<td>'+str(s['bad_rate'])+'</td>'+'<td>'+str(s['woe'])+'</td>'+'<td>'+str(s['category_t'])+'</td>'+'</tr>'
        html_file.write(content)
'''


def generate_table_item_html(s, html_file, var_name, type):
    if type == 'object':
        content = '<tr>' + '<td>' + str(s['bin_num']) + '</td>' + '<td>' + str(s[var_name]) + '</td>' \
                  + '<td>' + str(s['bads']) + '</td>' + '<td>' + str(s['goods']) + '</td>' + '<td>' + str(
            s['total']) + '</td>' + '<td>' + str(s['total_perc']) + '</td>' + '<td>' + str(
            s['bad_rate']) + '</td>' + '<td>' + str(s['woe']) + '</td>' + '<td>' + str(
            s['category_t']) + '</td>' + '</tr>'
    else:
        content = '<tr>' + '<td>' + str(s['bin_num']) + '</td>' + '<td>' + str(s['min']) + '</td>' + '<td>' + str(
            s['max']) + '</td>' + '<td>' + str(s['bads']) + '</td>' + '<td>' + str(s['goods']) + '</td>' + '<td>' + str(
            s['total']) + '</td>' + '<td>' + str(s['total_perc']) + '</td>' + '<td>' + str(
            s['bad_rate']) + '</td>' + '<td>' + str(s['woe']) + '</td>' + '<td>' + str(
            s['category_t']) + '</td>' + '</tr>'

    html_file.write(content)


def generate_bin_table_html(df_map, var_name, type, html_file):
    if type == 'object':
        html_file.write('<table border="1">')
        html_file.write('<caption>')
        html_file.write(var_name)
        html_file.write('</caption>')
        html_file.write('<tr><th>bin_num</th><th>value</th><th>bads</th><th>goods</th><th>total</th><th>total_perc</th><th>bad_rate</th><th>woe</th><th>category_t</th><tr>')
        df_map.apply(lambda s: generate_table_item_html(s, html_file, var_name, type), axis=1)
        html_file.write('</table>')
    else:
        html_file.write('<table border="1">')
        html_file.write('<caption>')
        html_file.write(var_name)
        html_file.write('</caption>')
        html_file.write('<tr><th>bin_num</th><th>min</th><th>max</th><th>bads</th><th>goods</th><th>total</th><th>total_perc</th><th>bad_rate</th><th>woe</th><th>category_t</th><tr>')
        df_map.apply(lambda s: generate_table_item_html(s, html_file, var_name, type), axis=1)
        html_file.write('</table>')


def generate_bin_table_html_single(df_map, var_name, type, html_file):
    if type == 'object':
        html_file.write('<table border="1">')
        html_file.write('<caption>')
        html_file.write(var_name)
        html_file.write('</caption>')
        html_file.write('<tr><th>bin_num</th><th>value</th><th>bads</th><th>goods</th><th>total</th><th>total_perc</th>><th>bad_rate</th><th>woe</th><th>category_t</th><tr>')
        df_map.apply(lambda s: generate_table_item_html(s, html_file, var_name, type), axis=1)
        html_file.write('</table>')
    else:
        html_file.write('<table border="1">')
        html_file.write('<caption>')
        html_file.write(var_name)
        html_file.write('</caption>')
        html_file.write('<tr><th>bin_num</th><th>min</th><th>max</th><th>bads</th><th>goods</th><th>total</th><th>total_perc</th><th>bad_rate</th><th>woe</th><th>category_t</th><tr>')
        df_map.apply(lambda s: generate_table_item_html(s, html_file, var_name, type), axis=1)
        html_file.write('</table>')


def plot_woe_pic(new_df_map, variable, pic_path):
    bin_num_value = np.array(new_df_map['bin_num'])[::-1]
    woe_value = new_df_map['woe'].tolist()
    idx = np.arange(len(bin_num_value))
    # plt.barh(bin_num_value, woe_value, color='b')
    # plt.yticks(idx + 0.4, bin_num_value)
    # plt.grid(axis='x')

    # plt.xlabel(str(v)+' woe value')
    # plt.ylabel('Bin Number')
    # plt.savefig(pic_path, dpi=100)
    # plt.close()


def plot_woe_pic_single(new_df_map, variable, pic_path):
    bin_num_value = np.array(new_df_map['bin_num'])[::-1]
    woe_value = new_df_map['woe'].tolist()
    idx = np.arange(len(bin_num_value))
    # plt.barh(bin_num_value, woe_value, color='b')
    # plt.yticks(idx -0.6, bin_num_value)
    # plt.grid(axis='x')

    # plt.xlabel(str(v)+' woe value')
    # plt.ylabel('Bin Number')
    # plt.savefig(pic_path, dpi=100)
    # plt.close()

def single_numerical(df_train, df_test, my_var, my_target, my_boundary_list):
    my_html_file = open('Adjust_Binning.html', 'w')
    my_var_type = str(df_train[my_var].dtype)
    my_result_0 = get_manual_bin_numeric(df_train, my_var, my_target, my_boundary_list,0)
    my_result = my_result_0['df_woe']
    my_result_all = my_result_0['df_result']
    new_name = my_var + '_woe'
    df_train[new_name] = 0
    df_train.drop(new_name, axis = 1, inplace = True)
    df_train_woe = df_train.join(my_result_all[new_name])
    #    print my_result
    my_iv = my_result.IV.sum()
    print my_var

    my_html_file.write('<b>' + 'training' + '</b>')
    my_html_file.write('<br>')
    my_html_file.write('<b>IV: ' + str(my_iv) + '</b>')
    generate_bin_table_html_single(my_result, my_var, my_var_type, my_html_file)
    my_html_file.write('<br>')
    boundary_list = str(my_boundary_list)
    my_html_file.write(boundary_list)
    my_html_file.write('<br>')
    pic_path = './pic_single/' + my_var + '_train.png'
    plot_woe_pic_single(my_result, my_var, pic_path)
    my_html_file.write("<img src='%s' width='450px' height='300px' />" % pic_path)
    my_html_file.write('<hr>')
    #    my_html_file.close()
    #
    #    my_html_file = open('single_var_test.html', 'w')
    my_var_type = str(df_test[my_var].dtype)
    my_result_0 = get_manual_bin_numeric(df_test, my_var, my_target, my_boundary_list,0)
    my_result = my_result_0['df_woe']
    my_result_all = my_result_0['df_result']
    new_name = my_var + '_woe'
    df_test[new_name] = 0
    df_test.drop(new_name, axis = 1, inplace = True)
    df_test_woe = df_test.join(my_result_all[new_name])
    #    print my_result
    my_iv = my_result.IV.sum()
    #    print my_var
    my_html_file.write('<b>' + 'testing' + '</b>')
    my_html_file.write('<br>')
    my_html_file.write('<b>IV: ' + str(my_iv) + '</b>')
    generate_bin_table_html_single(my_result, my_var, my_var_type, my_html_file)
    boundary_list = str(my_boundary_list)
    my_html_file.write(boundary_list)
    my_html_file.write('<br>')
    pic_path = './pic_single/' + my_var + '_test.png'
    plot_woe_pic_single(my_result, my_var, pic_path)
    my_html_file.write("<img src='%s' width='450px' height='300px' />" % pic_path)
    my_html_file.write('<hr>')
    my_html_file.close()
    return [df_train_woe, df_test_woe]


def single_numerical_no_html(df_train, type, my_var, my_target, my_boundary_list):
    if type is False:
        my_result_0 = get_manual_bin_numeric(df_train, my_var, my_target, my_boundary_list, 0)
    else:
        my_result_0 = get_manual_bin_categorical(df_train,my_var,my_target,my_boundary_list)
    my_result = my_result_0['df_woe']
    my_result_all = my_result_0['df_result']
    # new_name = my_var + '_woe'
    # df_train[new_name] = 0
    # df_train.drop(new_name, axis = 1, inplace = True)
    # df_train_woe = df_train.join(my_result_all[new_name])
    #    print my_result
    return [my_result,my_result_all]


def single_categorical(df_train, df_test, my_var, my_target, my_boundary_list):
    my_var_type = str(df_train[my_var].dtype)
    unique_value = len(df_train[my_var].value_counts())
    max_cardinality = 10
    if unique_value <= max_cardinality:
        my_var_type = 'object'

    my_result_0 = get_manual_bin_categorical(df_train, my_var, my_target, my_boundary_list)
    my_result = my_result_0['df_woe']
    my_result_all = my_result_0['df_result']
    new_name = my_var + '_woe'
    df_train[new_name] = 0
    df_train.drop(new_name, axis = 1, inplace = True)
    df_train_woe = df_train.join(my_result_all[new_name])
    my_iv = my_result.IV.sum()

    my_html_file = open('Adjust_Binning.html', 'w')
    my_html_file.write('<b>' + 'training' + '</b>')
    my_html_file.write('<br>')
    my_html_file.write('<b>IV: ' + str(my_iv) + '</b>')
    generate_bin_table_html_single(my_result, my_var, my_var_type, my_html_file)
    boundary_list = str(my_boundary_list)
    my_html_file.write(boundary_list)
    my_html_file.write('<br>')
    pic_path = './pic_single/' + my_var + '_train.png'
    plot_woe_pic_single(my_result, my_var, pic_path)
    my_html_file.write("<img src='%s' width='450px' height='300px' />" % pic_path)
    my_html_file.write('<hr>')

    my_result_0 = get_manual_bin_categorical(df_test, my_var, my_target, my_boundary_list)
    my_result = my_result_0['df_woe']
    my_result_all = my_result_0['df_result']
    new_name = my_var + '_woe'
    df_test[new_name] = 0
    df_test.drop(new_name, axis = 1, inplace = True)
    df_test_woe = df_test.join(my_result_all[new_name])
    my_iv = my_result.IV.sum()

    my_html_file.write('<b>' + 'testing' + '</b>')
    my_html_file.write('<br>')
    my_html_file.write('<b>IV: ' + str(my_iv) + '</b>')
    generate_bin_table_html_single(my_result, my_var, my_var_type, my_html_file)
    boundary_list = str(my_boundary_list)
    my_html_file.write(boundary_list)
    my_html_file.write('<br>')
    pic_path = './pic_single/' + my_var + '_test.png'
    plot_woe_pic_single(my_result, my_var, pic_path)
    my_html_file.write("<img src='%s' width='450px' height='300px' />" % pic_path)
    my_html_file.write('<hr>')
    return [df_train_woe, df_test_woe]

# print '\xe4\xbb\xa3\xe4\xbb\x98\xe5\xb7\xa5\xe8\xb5\x84\xe6\x9c\x88\xe8\x96\xaa'.encode("utf8")
a =  '\xe4\xbb\xa3\xe4\xbb\x98\xe5\xb7\xa5\xe8\xb5\x84\xe6\x9c\x88\xe8\x96\xaa'.decode('utf8')

#********************************************

#
# given a boundary_list, return a table with binning result and woe
#
def get_single_variable_bin(df_train, type, my_var, my_target, boundary_list):
    #need to adjust later
    if type is False:
        type = 'Numerical'
    else:
        type = 'Categorical'

    if type == 'Numerical':
        bin_result = get_single_variable_bin_numeric(df_train, my_var, my_target, boundary_list)
    else:
        bin_result = get_single_variable_bin_categorical(df_train,my_var,my_target, boundary_list)

    return bin_result


def get_single_variable_bin_numeric(df, var, target, boundary_list):
    # process single variable
    # sort the boundary_list
    boundary_list_high_to_low_sort = sorted(boundary_list, cmp_with_nan)
    df_cur = df[[var, target]].copy()
    bin_map = get_boundary_mapping(boundary_list_high_to_low_sort)
    df_cur['bin_num'] = df_cur[var].apply(lambda x: bin_assign(bin_map, x))
    df_woe = get_single_var_bin_woe(df_cur, var, 'bin_num', target, boundary_list)
    df_woe['type'] = 'Numerical'
#    df_woe.sort(['bin_num'], ascending=[1], inplace=True)

    return df_woe


def get_single_variable_bin_categorical(df, var, target, bin_list):
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
    df_final[var+'_woe'] = df_final['bin_num_woe']
    del df_final['bin_num_woe']

    return {'df_woe': df_woe, 'df_result': df_final}


def cmp_with_nan(x, y):
    if str(x) == 'nan':
        return 1
    if str(y) == 'nan':
        return -1
    if x < y:
        return 1
    if x > y:
        return -1
    return 0


def get_boundary_mapping(boundary_list):
    # as a list to keep the order: high to low, nan as the last
    return zip(boundary_list, range(0, len(boundary_list))[::-1])


def get_boundary_mapping_reverse(max_boundary_list,pre=0):
    max_min_boundary = []
    # pre = np.nan
    for mb in max_boundary_list:
        max_min_boundary.append((pre, mb))
        pre = mb
    max_min_boundary.append((pre, np.nan))

    #    max inclusive, min exclusive
    return dict(zip(range(0, len(max_min_boundary)), max_min_boundary))


def bin_assign(bin_map, v):
    #sort my order to high to low accroding to the key
    for key, value in bin_map:
        if v >= key:
            return value
    return 0


def get_single_var_bin_woe(df, var_ori, var_bin, target, boundary_list):
    """
    assume: good is 0 and bad is 1
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

    agg['odds'] = ((agg.goods + delta_1) / (agg.bads + delta_1))
    agg['oddsrt'] = (agg['odds'] / (float(agg.goods.sum()) / float(agg.bads.sum())))
    agg['woe'] = agg['oddsrt'].apply(lambda x: np.round(np.log(x) * 1.0, 4))
    agg['IV'] = (agg.goods / sum(agg.goods) - agg.bads / sum(agg.bads)) * agg.woe
    agg['bad_rate'] = (agg.bads / agg.total).apply('{0:.2%}'.format)

    agg.sort_values(['bin_num'], ascending=[1], inplace=True)
    #agg['ks'] = ((agg.bads / ddt.bad.sum()).cumsum() - (agg.goods / ddt.good.sum()).cumsum())
    agg['total_perc'] = (agg.total / float(ddt.bad.sum() + ddt.good.sum())).apply('{0:.2%}'.format)
    agg['min_boundary'] = boundary_list[:-1]

    min_shift = agg['min'][1:].append(pd.Series(np.inf))
    min_shift = min_shift.reset_index()
    del min_shift['index']
    agg['max_boundary'] = min_shift
    agg['max_boundary'] = agg['max_boundary']
    del agg['oddsrt']

    return agg


def get_tree_bin(df, var, target, varType, nullValue=[], treeDep=3, minLeafRate=0.05):
    '''
    porcess single variable
    '''

    minLeaf=int(len(df) * minLeafRate)
    for nv in nullValue:
        df[var] = df[var].replace(nv, np.nan)

    org_var = var
    type = 'Numerical'
    #category_t = False

    if varType in ['object', 'str']:
        df = transform_categorical_to_woe(df, var, target)
        var += '_woe'
        type = 'Categorical'

    ss = decision_tree_bin(df, var, target, varType, tree_dep=treeDep, min_leaf=minLeaf)

    var += '_tree_bin'
    df_cur = ss['df_bin']
    df_cur[var] = df_cur['bad_hood_woe']
    df_cur['bad'] = df_cur[target]
    df_cur['good'] = 1 - df_cur[target]
    if type == 'Categorical':
        org_var_woe = org_var + '_woe'
        df_cur_grp = df_cur.groupby(var)
        df_mapping = pd.DataFrame(
            {'total': df_cur_grp.size(),
             org_var: df_cur_grp[org_var].unique(),
             'bads': df_cur_grp['bad'].sum(),
             'goods': df_cur_grp['good'].sum(),
             'bad_rate': (df_cur_grp['bad'].sum().astype(float)/df_cur_grp.size())
             }).reset_index()
        df_mapping['bad_rate'] = df_mapping['bad_rate'].apply('{0:.2%}'.format)
    else:
        df_cur_grp = df_cur.groupby(var)
        #df_mapping = df_group[org_var].agg({'max': np.max, 'min': np.min, 'total': np.size, 'bads': np.bad.sum()}).reset_index()
        df_mapping = pd.DataFrame(
            {'total': df_cur_grp.size(),
             'max': df_cur_grp[org_var].max(),
             'min': df_cur_grp[org_var].min(),
             'bads': df_cur_grp['bad'].sum(),
             'goods': df_cur_grp['good'].sum(),
             'bad_rate': (df_cur_grp['bad'].sum().astype(float) / df_cur_grp.size())
             }).reset_index()
        df_mapping['bad_rate'] = df_mapping['bad_rate'].apply('{0:.2%}'.format)

    df_mapping['type'] = type
    df_mapping.rename(columns={var: 'woe'}, inplace=True)
    df_mapping['total_perc'] = (df_mapping.total/float(df_mapping.total.sum())).apply('{0:.2%}'.format)

    if type == 'Categorical':
        df_mapping.sort_values(['total'], ascending=[1], inplace=True, na_position='first')
        df_mapping['bin_num'] = range(len(df_mapping))
        df_mapping = df_mapping[['bin_num', org_var, 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe', 'type']]
    else:
        # df_mapping['max'] = df_mapping['max'].apply('{0:.15%}'.format())
        df_mapping.sort_values(['min'], ascending=[1], inplace=True, na_position='first')
        if df_mapping['min'].isnull().values.any():
            df_mapping['bin_num'] = range(len(df_mapping))
        else:
            df_mapping['bin_num'] = [i+1 for i in range(len(df_mapping))]
        # put the nan to the first row
        #reindex_list = [i+1 for i in range(len(df_mapping)-1)]
        #reindex_list.append(0)
        #df_mapping['bin_num'] = reindex_list
        #df_mapping.sort_values(['bin_num'], ascending=[1], inplace=True)
        df_mapping['max'] = df_mapping['max'].apply('{:.15f}'.format)
        df_mapping['min'] = df_mapping['min'].apply('{:.15f}'.format)
        df_mapping = df_mapping[['bin_num', 'min', 'max', 'bads', 'goods', 'total', 'total_perc', 'bad_rate', 'woe', 'type']]

    df_mapping['index'] = range(len(df_mapping))
    df_mapping = df_mapping.set_index(['index'])

    return {'IV': sum(ss['df_woe'].IV), 'type': type, 'df_map': df_mapping}

