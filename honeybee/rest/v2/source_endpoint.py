# coding=utf-8
import logging as log
import json

import pandas as pd
from flask import request

import util.restful_tools as rest
from beans import tool_model
from common.exceptions import Error
from rest.app_base import app
from service import basic_analysis_service as basic_analysis
from service.db import source_service
from service.db import variable_service
from util import common, simple_util
from common.constant import DBConstant


@app.route("/source/add", methods=['post'])
def add_source():
    """
    新增一个数据源
    :param:projectId 所属工程的id
    :param:fileScope 文件可被访问的范围
    :param:fileName 上传的文件的别名
    :param:dataSet 上传的文件
    :param:fileOrigin 文件的来源
    :return: 是否添加成功 True or False
    """
    form = request.form
    source = tool_model.Source()
    source.project_id = form.get('projectId')
    source.file_scope = form.get('fileScope')
    source.set_name = form.get('setName')  # 用户为dataset指定的别名
    source.file_origin = form.get('fileOrigin')  # 文件的来源

    # 获取上传的文件并判断
    files = request.files.getlist("dataSet")
    if len(files) <= 0:
        # 若文件不存在时将会抛出异常
        raise Error.SOURCE_NO_FILE_RECEIVED

    f = files[0]
    from unicodedata import normalize
    filename = normalize('NFKD', f.filename).encode('utf-8', 'ignore')
    file_path = app.config['ROOT_PATH'] + "/" + filename
    # 保存文件至本地
    f.save(file_path)
    size = simple_util.get_file_size(file_path)

    source.file_name = filename  # 上传的文件的名称
    source.file_path = file_path
    source.file_size = size

    result = source_service.add_source(source)[0]
    return rest.responseto(result)


@app.route("/source/parse/<string:source_id>", methods=['post'])
def source_parse(source_id):
    """
    在文件上传完成后直接进行解析,解析的结果会保存到数据库中
    :param source_id:
    :return:
    """
    source = source_service.get_sources(source_id)
    file_path = source.file_path
    df = None
    is_success = True
    # 先判断文件类型,再使用pandas解析文件
    if file_path.endswith("xlsx"):
        df = pd.read_excel(source.file_path)
    elif file_path.endswith("csv"):
        df = pd.read_csv(file_path)
    try:
        df_sum = basic_analysis.get_df_summary(df)
        # 得到df_train,将dataframe转换为用于展示前端展示的数据
        data_list = common.df_for_db(df_sum)

        # 将解析完的变量存入数据库中
        variables = []
        for data in data_list:
            variable = tool_model.Variable()
            variable.__dict__ = data
            variable.variable_name = data['variable']
            variable.usage = 0
            variables.append(variable)
        variable_service.save_variables(variables)

        source = tool_model.Source()
        source.file_readable = DBConstant.SOURCE_FILE_READABLE
    except Exception, e:
        log.error(e, exc_info=1)
        source.file_readable = DBConstant.SOURCE_FILE_UNREADABLE
        is_success = False

    source_service.update_source(source)

    if is_success:
        return rest.responseto(message="successful parse the data set")
    else:
        raise Error.SOURCE_PARSE_FAIL


@app.route("/source/update/<string:source_id>", methods=['post'])
def update_source(source_id):
    set_name = request.form.get("setName")
    if_delete = request.form.get("ifDelete")

    source = tool_model.Source()
    source.id = source_id
    source.set_name = set_name
    source.is_deleted = 1 if bool(if_delete) else None

    num = source_service.update_source(source)
    if num > 0:
        return rest.responseto("")
    else:
        raise Error.SOURCE_UPDATE_FAIL


@app.route("/source/list/<string:project_id>", methods=['get'])
def list_source(project_id):
    """
    罗列该工程所有的资源
    :param project_id: 所属工程的id
    :return: 所有资源的相关参数
    """
    sources = source_service.get_sources(project_id=project_id)

    def map_value(source):
        d = {'setName': source.set_name, 'fileName': source.file_name, 'fileSize': source.file_size,
             'fileScope': source.file_scope, 'addAt': source.create_date.isoformat(sep=" "),
             'origin': source.file_origin,
             'readable': source.file_readable}
        return d

    list = map(map_value, sources)
    return rest.responseto(list)


def del_source(source_id):
    """
    删除数据集和与之相关的所有变量记录
    :param source_id:
    :return:
    """
    source_service.delete_source_by_id()
