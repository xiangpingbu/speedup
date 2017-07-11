# coding=utf-8

import re
import csv

file_path = "/homes/upload/qianmi/log_mobile_service.sql"
# file_path = "/Users/lifeng/Desktop/data.txt"
##得到一行数据中,被''引用的字段
pattern = "\'.*?\'"


file_name = '/homes/upload/qianmi/users/customer_no_'
# file_name = '/Users/lifeng/Desktop/users/customer_no_'
head = ['id','customer_no','auth_id','add_time','info','log_status','json_status']

index = 0

with open(file_path, 'rb') as f:
    for line in f:
        line =  line.rstrip("\r\n")
        if 'INSERT' not in line:
            continue
        first =  line.find('(')
        last =  line.find(')')
        # print line[first+1:last]

        guid = re.findall(pattern, line, re.M)
        row = map(lambda x:x.replace('\'', '').replace('\'', ''),guid)
        row[4] = row[4].replace("\\\"","\"")
        file_object = open(file_name+row[1], 'w')
        writer = csv.writer(file_object,delimiter='|',quotechar=' ')
        writer.writerow(head)
        writer.writerow(row)
        file_object.close()
        index +=1

        print index

        if index ==200:
            break


