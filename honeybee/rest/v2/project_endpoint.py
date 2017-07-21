# coding=utf-8
from service.db import project_service
from rest.app_base import app
from beans import tool_model
from util import restful_tools as rest,simple_util
from flask import request
from common.constant import DBConstant


@app.route("/project/list/<string:user_id>", methods=['get'])
def get_projects(user_id):
    """
    获得该用户所有的工程
    :return:
    """
    query = project_service.get_projects(user_id)
    projects = simple_util.query_to_base(query)
    for project in projects:
        project['project_task'] = DBConstant.PROJECT_TASK[int(project['project_task'])]
    return rest.responseto(projects)


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


