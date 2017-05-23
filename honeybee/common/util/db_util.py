# coding=utf-8
def process_query(sql,params):
    """
    query查询 sql通用化
    :param sql: 根据param中的key拼接sql
    :param params: 执行sql时要查询的字段和值
    :return: [sql,list]
    """
    list = []
    if params is not None:
        sql += " where "
        index = 0
        for key, value in params.items():
            sql += key + "=%s"
            if index != len(params) - 1:
                sql += " and "
            list.append(value)
            index +=1
    return [sql,list]

def process_insert(sql,params):
    key_list = []
    value_list = []
    for key,value in params.items():
        key_list.append(key)
        value_list.append(value)

    sql+=" ( "+",".join(key_list)+") values"

    sql+= " ("+','.join((map(lambda x: '%s', value_list))) + ")"

    return [sql,value_list]






