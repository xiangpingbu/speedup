# -*- coding: utf-8 -*-

import pandas as pd
import numpy as np
import xlrd
import json
import re

import sys
reload(sys)
sys.setdefaultencoding('utf8')

def load_xlsx(path, sheet_name):
    xl = pd.ExcelFile(path)
    df = xl.parse(sheet_name, header=None)

    sub_df = pd.DataFrame()
    start_row_index = -1
    # end_row_index = 0

    num_header_list = ["bin_num", "min_boundary", "max_boundary", "woe", "coefficient"]
    cate_header_list = ["bin_num", "name", "woe", "coefficient"]

    df_list=[]
    df_map = {}
    for index, row in df.iterrows():
        if df.iloc[index,0] is np.nan:
            # end_row_index = index
            sub_df = df.iloc[start_row_index+1:index, :]
            start_row_index = index
            # sub_df = sub_df.assign(type='')
            if not sub_df.empty:
                if len(sub_df)<2:
                    continue;
                # check if the type is numerical or categorical
                elif sub_df.iloc[1,1:3].tolist() == [u'min', u'max']:
                    # Numerical
                    standard_df = sub_df.iloc[2:,:5].rename(columns=(lambda x: num_header_list[x]), inplace=False)
                    standard_df = standard_df.assign(type='Numerical')
                else:
                    # Categorical
                    standard_df = sub_df.iloc[2:,:4].rename(columns=(lambda x: cate_header_list[x]), inplace=False)
                    standard_df = standard_df.assign(type='Categorical')
                    standard_df.rename(columns={"name":sub_df.iloc[0, 1]}, inplace=True)
                #     pass
                # df_list.append(standard_df)
                standard_df = standard_df.astype(str)
                jsonStr = standard_df.to_json(orient='records', force_ascii=False)
                newJsonStr = re.sub('\.0\"', '\"', jsonStr)

                # print sub_df
                # print newJsonStr
                df_map[sub_df.iloc[0,1]] = newJsonStr

                # print df_map
                # outputPath = '/Users/pailie/Documents/Work/maas/honeybee/local/'+sub_df.iloc[0,1]+'.json'
                outputPath = '/Users/pailie/Documents/Work/social_insurance/scorecard/'+sub_df.iloc[0,1]+'.json'
                file_object = open(outputPath, 'w')
                file_object.write(newJsonStr.encode("utf-8"))
                file_object.close()


    # print df_list[0].iloc[1,1:3]==pd.Series(['min', 'max'])
    # print df_list[2]
    # temp = json.dumps(df_map, ensure_ascii=False)
    # print temp

    # outputPath = '/Users/pailie/Documents/Work/maas/honeybee/local/test.json'
    # file_object = open(outputPath, 'w')
    # file_object.write(temp.encode("utf-8"))
    # file_object.close()

        # print df.iloc[index,0] is np.nan
    # print df

if __name__ == '__main__':
    path = "/Users/pailie/Downloads/社保评分卡.xlsx"
    # path = "/Users/pailie/Downloads/公积金评分卡.xlsx"
    sheet_name = "scorecard"

    load_xlsx(path, sheet_name)

#
# xl = pd.ExcelFile(path)
# df = xl.parse(sheet_name, header=None).loc[:,:4]
#
# print df

# print df[0].isnull()

# for index, row in df.iterrows():
#     if df.iloc[index, 0] == np.nan:

# print df

# header_list = worksheet.row_values(1)[:4]
# header_list = ["bin_num", "min_boundary", "max_boundary", "bads", "goods", "total", "total_perc", "bad_rate", "woe"]
# header_list = ["bin_num", "min_boundary", "max_boundary", "range", "bads", "goods", "total", "total_perc", "bad_rate", "woe"]
# header_list = ["bin_num", "min_boundary", "max_boundary", "woe", "coefficient"]




# outputPath = '/Users/pailie/Documents/Work/maas/honeybee/local/balance.json'
# file_object = open(outputPath, 'w')
# file_object.write(temp.encode("utf-8"))
# file_object.close()
