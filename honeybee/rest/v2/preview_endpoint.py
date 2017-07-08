# coding=utf-8
import json
from flask import request

from rest.app_base import app
from beans import tool_model
from service.db import variable_service
from util import restful_tools as rest


@app.route("/source/preview/<string:source_id>", methods=['post'])
def preview(source_id):
    """
    预览某个数据集的数据
    :param source_id: 所属数据集id
    :return: list of Variable, 变量的列表
    """
    variables = variable_service.get_variables(source_id)
    # return rest.responseto(variables,cls=tool_model.AlchemyEncoder)
    return json.dumps(variables, cls=tool_model.AlchemyEncoder)


@app.route("/source/preview/select/<string:source_id>", methods=['post'])
def use_or_not(source_id):
    """
    预览数据集后,选择有价值的变量
    :return: 如果操作不顺利,异常在外层将会被拦截
    """
    form = request.form
    selected_variable_str = form.get('selected')
    target = form.get('target')

    selected_variable_list = None
    if selected_variable_str is not None:
        selected_variable_list = selected_variable_str.split(",")
    variable_service.set_variable_selected(source_id,selected_variable_list,target)

    return rest.responseto("")


