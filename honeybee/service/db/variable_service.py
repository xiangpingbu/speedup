# coding=utf-8
from common.db.mysql import db_util

def save_variables(variables):
    """
    保存多个变量
    :param variables: variable list
    :return: 主键 list
    """
    return db_util.add_all(variables)


