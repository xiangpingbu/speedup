# coding=utf-8
import const

const.MAAS_HOST = 'http://localhost:8082'
const.SELECTED = 1


class DBConstant():
    SOURCE_FILE_READABLE = 0  # 数据集可以被解析
    SOURCE_FILE_UNREADABLE = 1  # 数据集不能被解析

    VARIABLE_UNUSED = 0  # 不被使用的变量
    VARIABLE_USED = 1  # 需要使用的变量
    VARIABLE_TARGET = 3  # 作为target使用的变量
