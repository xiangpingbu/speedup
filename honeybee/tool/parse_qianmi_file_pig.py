# coding=utf-8
import re
import json
import types
import time


def parse(content):
    t = type(content)
    if t is types.ListType:
        # if len(content) > 0 and type(content[0]) is types.DictionaryType:
        #     return ''
        # else:
        return ",".join(content).encode('utf8')
    elif t is types.UnicodeType:
        return content.encode('utf8')
    else:
        return content

pattern = "\'.*?\'"


@outputSchema("row:chararray")
def load(bag):
    start = time.time()
    try:
        for ele in bag:
            line = ele[0]
        # print line[first+1:last]
            line = line.replace("\\'\\'", "")
            guid = re.findall(pattern, line, re.M)
            row = map(lambda x: x.replace('\'', '').replace('\'', ''), guid)
            # 说明这一行数据是老数据
            if row[6] == '1':
                json_obj = json.loads(row[4].replace("\\\"", "\""), encoding='utf8')
                # 获得json主体内容
                json_obj = json_obj.get('content', None)
                if json_obj == None or len(json_obj) == 0: continue
                ##基础信息
                basic = json_obj['input_info']
                id_card = basic['id_card']
                phone = basic['phone']
                customer_no = row[1]
                user_name = basic['user_name'].encode('utf-8')
                basic_head = ['customer_no', 'id_card', 'phone', 'user_name']
                basic_body = [customer_no, id_card, phone, user_name]

                special_call_head = ['talk_seconds', 'talk_cnt']
                special_call_head = basic_head + special_call_head
                special_call_row = None
                for key in json_obj.keys():
                    ##这部分数据相较于其他有一些特殊,需要区别对待.
                    if key == 'user_portrait':
                        special = json_obj[key]
                        head = ['both_call_cnt', 'radio', 'location', 'night_activity_radio']
                        row = [special['both_call_cnt'],
                               special['contact_distribution']['ratio'],
                               special['contact_distribution']['location'].encode('utf-8'),
                               special['night_activity_ratio']]
                        return str(row)
                    # if key == 'trip_analysis':
                    #     special = json_obj[key]
                    #     for s in special:
                    #         detail = s.get('detail', None)
                    #         if detail != None:
                    #             for item in detail:
                    #                 head = item.keys()
                    #                 row = map(lambda x: parse(item[x]), head)
                    #                 writer = getWriter("trip_analysis_detail", basic_head + head)
                    #                 writer.writerow(basic_body + row)
                    # o = json_obj[key]
                    # for content in o:
                    #     if type(o) is types.DictionaryType:
                    #         head = o.keys()
                    #         row = map(lambda x: parse(o[x]), head)
                    #     else:
                    #         head = content.keys()
                    #         row = map(lambda x: parse(content[x]), head)
                    #
                    #     head = basic_head + head
                    #     row = basic_body + row
                    #     # writer = None
                    #     writer = getWriter(key, head)
                    #     writer.writerow(row)
            # else:
            #     # 这一行数据为新数据
            #     content = row[4].replace("\\\"", "\"")
            #     print row[0], row[1], row[2], row[3]
            #     json_obj = json.loads(content)
            #     data = json_obj['wd_api_mobilephone_getdata_response'][u'data'][u'data_list'][0]
            #     user_data = data['userdata']
            #
            #     id_card = user_data['id_card'].encode("utf-8")
            #     phone = user_data['phone']
            #     customer_no = row[1]
            #     user_name = user_data['real_name'].encode('utf-8')
            #
            #     basic_head = ['customer_no', 'id_card', 'phone', 'user_name']
            #     basic_body = [customer_no, id_card, phone, user_name]
            #     for key in data.keys():
            #         if key == 'billdata':
            #             bill_data = data[key]
            #             for content in bill_data:
            #                 head = content.keys()
            #                 row = map(lambda x: parse(content[x]), head)
            #                 head = basic_head + head
            #                 row = basic_body + row
            #                 writer = getWriter(key, head)
            #                 writer.writerow(row)
            #
            #         elif key == 'msgdata' or key == 'teldata':
            #             sub_data = data[key]
            #             for content in sub_data:
            #                 content = content[key]
            #                 for inner in content:
            #                     head = inner.keys()
            #                     row = map(lambda x: parse(inner[x]), head)
            #                     head = basic_head + head
            #                     row = basic_body + row
            #                     writer = getWriter(key, head)
            #                     writer.writerow(row)
            #         elif key == 'userdata':
            #             sub_data = data[key]
            #             head = sub_data.keys()
            #             row = map(lambda x: parse(sub_data[x]), head)
            #             head = ['customer_no'] + head
            #             row = [customer_no] + row
            #             writer = getWriter(key, head)
            #             writer.writerow(row)
    except Exception, e:
        print e
    end = time.time()
    print end - start
