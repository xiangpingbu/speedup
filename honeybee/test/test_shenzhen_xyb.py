# coding=utf-8
import pandas
import requests
import json
import csv

##测试一组数据并获得分数,导出为一个文件

url = "http://10.10.10.200:18080/ecreditpal/rest/api/v2"
file_path = "/Users/lifeng/Documents/data_for_XYB_score.csv"
file_out_path = "/Users/lifeng/Documents/XYB_score.csv"

file_object = open(file_out_path, 'w')
writer = csv.writer(file_object)
writer.writerow(
    ['index', 'customer_no', 'age', 'cid', 'mobile', 'zhiMaCredit', 'gender', 'p_id_city_same_ind', 'score'])

i = -1
for line in open(file_path, "r").readlines():
    i += 1
    if i == 0: continue
    row = line.rstrip("\r\n").split(",")
    index = row[0]
    customer_no = row[1]
    age = row[2]
    cid = row[3]
    mobile = row[4]
    zhi_ma_credit = row[5]
    gender = row[7]
    p_id_city_same_ind = row[8]

    payload = {'age': age,
               'cid': cid,
               'mobile': mobile,
               'zhiMaCredit': zhi_ma_credit,
               'gender': gender,
               'apiCode': 'M113',
               'account': 'lifeng',
               'password': 'MTIz'}
    response = requests.post(url=url, data=payload).text
    r = json.loads(response)
    try:
        data = r["data"]['MAAS.M113']['result']['data']
        score = int(data)
    except Exception, e:
        print Exception, ":", e
        score = 0
    row.append(score)
    writer.writerow(row)
    # print response

file_object.close()
