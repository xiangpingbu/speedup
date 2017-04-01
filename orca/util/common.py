# coding=utf-8
def transfer(str):
    """将字符串转化为utf8"""
    return str.encode('utf-8')

# def transferNan(str):

def replace(str):
    """将字符串中的|替换为,"""
    return str.replace("|",",")


def df_for_html(df):
    dataMap = {}
    head = []
    for column in df.columns:
        head.append(str(column))

    dataMap["body"] = []
    for index,row in df.iterrows():
        body = []
        dataMap["body"].append(body)
        for key in head:
            body.append(str(row[key]))

    dataMap["head"] = head

    return dataMap



