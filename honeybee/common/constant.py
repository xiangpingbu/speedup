# coding=utf-8
import const

const.MAAS_HOST = 'http://localhost:8082'
const.SELECTED = 1


class DBConstant():
    SOURCE_FILE_READABLE = 0  # 数据集可以被解析
    SOURCE_FILE_UNREADABLE = 1  # 数据集不能被解析
    SOURCE_AVAILABLE = 0  # 经过变量筛选前,变量不可用
    SOURCE_UNUSABLE = 1  # 经过变量筛选后,变量可用
    SOURCE_FILE_TYPE_CSV = 'csv'
    SOURCE_FILE_TYPE_EXCEL = 'csv'

    VARIABLE_UNUSED = 0  # 不被使用的变量
    VARIABLE_USED = 1  # 需要使用的变量
    VARIABLE_TARGET = 3  # 作为target使用的变量

    EXPERIMENT_LR = 0  # Logistic Regression
    EXPERIMENT_ALG_NAME = {0: 'Logistic Regression'}

    PROJECT_TASK = {0: 'Regression'}
