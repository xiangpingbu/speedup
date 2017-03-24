import os

from Binning_Function import *


def cal(df_train,target='bad_7mon_60'):
    ########################## dev vs val ##############################
    # df_train = pd.read_excel(train)
    # df_test = pd.read_excel(test)
    invalid_vars_list = [target]
    # from sklearn.cross_validation import train_test_split
    # target = df[['bad_7mon_60']]
    # df_train, df_test, y_train, y_test= train_test_split(df, target,test_size = 0.5,random_state = 40, stratify=target)

    ######################## get initial binning ########################
    df = df_train
    vars = df.columns
    print 'target is: ' + target
    df_iv = pd.DataFrame()
    iv_rank_map = {}

    for v in vars:
        if v not in invalid_vars_list:
            t = str(df[v].dtype)
            unique_value = len(df[v].value_counts())
            max_cardinality = 10
            if unique_value <= max_cardinality:
                t = 'object'
            print "current variable: " + v + " current type: " + t
            tree_bin = get_tree_bin(df, v, target, t, nullValue=[])
            # IV
            var_iv = tree_bin['IV']
            if t in ['object', 'str']:
                boundary_list = str(tree_bin['df_map'][v].tolist())
            else:
                boundary_list = str(tree_bin['df_map']['max'].tolist())

            new_bin = pd.DataFrame({"var_name": v, "iv": var_iv, "tree_boundary": boundary_list}, index=["0"])
            df_iv = df_iv.append(new_bin)

            # boundary
            new_df_map = tree_bin['df_map']
            iv_rank_map[v] = (v, t, new_df_map, boundary_list, var_iv)

    df_iv = df_iv[['var_name', 'iv', 'tree_boundary']]
    # df_iv.to_excel("df_iv.xlsx", ",", header=True, index=False)

    return iv_rank_map

##################### get html plot ######################################

# html_file = open('Initial_Binning.html', 'w')
# keys = iv_rank_map.keys()
# keys.sort(reverse=True)
# for k in keys:
#     c = iv_rank_map[k]
#     var_name = c[0]
#     var_type = c[1]
#     woe_map = c[2]
#     boundary = c[3]
#     html_file.write('<br>')
#     html_file.write('<b>IV: '+str(k)+'</b>')
#     generate_bin_table_html(woe_map, var_name, var_type, html_file)
#     html_file.write('<br>')
#     html_file.write(boundary)
#     html_file.write('<br>')
#     path = './pic_initial/' + var_name + '.png'
#     plot_woe_pic(woe_map, var_name, path)
#     html_file.write("<img src='%s' width='450px' height='300px' />" % path)
#     html_file.write('<hr>')
#
# html_file.close()

# cal("/Users/lifeng/Work/Code/fork/maas/orca/util/df_train.xlsx","/Users/lifeng/Work/Code/fork/maas/orca/util/df_test.xlsx")
