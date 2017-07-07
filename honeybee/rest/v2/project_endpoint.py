# coding=utf-8
from service.db import project_service
from rest.app_base import app
from beans import tool_model
import util.restful_tools as rest
from flask import request


@app.route("/project/add", methods=['post'])
def add_project():
    """
    新建一个工程
    :param:projectName 工程名称
    :param:projectTask 工程处理的模型类型
    :param:projectDesc 工程描述
    :return: 是否添加成功 True or False
    """
    form = request.form
    project = tool_model.Project()
    project.owner_id = form.get("ownerId")
    project.project_name = form.get("projectName")
    project.project_task = form.get("projectTask")
    project.project_desc = form.get("projectDesc")

    result = project_service.create_project(project)[0]
    return rest.responseto(result)


@app.route("/project/delete/<string:project_id>", methods=['post'])
def del_project(project_id):
    """
    删除一个工程
    :param project_id:  工程主键id
    :return: 是否删除成功 True or False
    """
    return project_service.delete_project(project_id)


