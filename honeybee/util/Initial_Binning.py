# coding=utf-8
import os

from Binning_Function import *

global MAX_CARDINALITY
MAX_CARDINALITY = 10


def cal(df_train,target,invalid = None):
    ########################## dev vs val ##############################
    # df_train = pd.read_excel(train)
    # df_test = pd.read_excel(test)
    invalid_vars_list = invalid
    if invalid_vars_list is None:
        invalid_vars_list=[]
    invalid_vars_list.append(target)
    # from sklearn.cross_validation import train_test_split
    # target = df[['bad_7mon_60']]
    # df_train, df_test, y_train, y_test= train_test_split(df, target,test_size = 0.5,random_state = 40, stratify=target)

    ######################## get initial binning ########################
    df = df_train
    vars = df.columns
    print 'target is: ' + target
    df_iv = pd.DataFrame()
    iv_rank_map = {}

    vars =[u'信用卡额度']
    #vars =[u'工作年限']
    for v in vars:
        if v not in invalid_vars_list:
            t = str(df[v].dtype)
            #unique_value = len(df[v].value_counts())
            #if unique_value <= MAX_CARDINALITY:
            #    t = 'object'
            print "current variable: " + v + " current type: " + t
            tree_bin = get_tree_bin(df, v, target, t, nullValue=[])
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

    df_iv = df_iv[['var_name', 'iv', 'tree_boundary']]
    # df_iv.to_excel("df_iv.xlsx", ",", header=True, index=False)

    return iv_rank_map
