# coding=utf-8
import json
from flask import request
import pandas

from rest.app_base import app
from beans import tool_model
from service.db import experiment_service, source_service, variable_service
from common.db.redis import redis_util
from service.model import lr_service
from util import restful_tools as rest
from common.constant import DBConstant


@app.route("/experiment/add", methods=['post'])
def add_experiment():
    """
    添加一个算法实验
    :param title: 试验名称
    :param project_id: 工程id
    :param source_id: 资源id
    :param algorithm: 算法代号
    :return: primary_key
    """
    form = request.form
    experiment = tool_model.Experiment()
    experiment.title = form.get('title')
    experiment.project_id = form.get('project_id')
    experiment.source_id = form.get('source_id')
    experiment.algorithm = form.get('algorithm')

    primary_key = experiment_service.create_experiment(experiment)
    return rest.responseto(primary_key)


@app.route("/experiment/list/<string:project_id>", methods=['post'])
def get_experiments(project_id):
    """
    得到工程下所有试验的详情
    :return:
    """
    for experiment in experiment_service.get_experiments(project_id):
        experiment.algorithm_name = DBConstant.EXPERIMENT_ALG_NAME[int(experiment.algorithm)]


@app.route("/experiment/init/<string:experiment_id>", methods=['post'])
def init_source(experiment_id):
    """
    初始化算法需要的初始数据
    :param experiment_id:  试验id
    :return:
    """
    out = redis_util.load('experiment_' + experiment_id)
    if out is not None:
        return rest.responseto(out)

    experiment = experiment_service.get_experiment(experiment_id)
    source = source_service.get_source(experiment.source_id)
    # 获得已经选择的变量,包括target
    selected_variable = variable_service.get_selected_variables(source.id)
    selected = selected_variable['used']
    selected_list = map(lambda x: x.variable_name, selected)
    target = selected_variable['target'].variable_name

    # 解析文件为dataframe
    data_frame = None
    if source.file_type == DBConstant.SOURCE_FILE_TYPE_CSV:
        data_frame = pandas.read_csv(source.file_path)
    elif source_service == DBConstant.SOURCE_FILE_TYPE_EXCEL:
        data_frame = pandas.read_excel(source.file_path)

    # 变量分bin初始化
    out = lr_service.get_init(data_frame, valid=selected_list, target=target, fineMinLeafRate=0)
    redis_util.dump('experiment_' + experiment_id, out)

    return rest.responseto(out)
