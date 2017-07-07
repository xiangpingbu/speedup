# coding=utf-8
from beans.tool_model import Project
from common.db.mysql import db_util


@db_util.error_handler
def create_project(project):
    """
    创建一个新的工程
    :param project: 工程信息
    :return:
    """
    return db_util.add_one(project)


def get_projects(owner_id):
    """
    获得用户的所有工程
    :param owner_id:  用户的id
    :return: Project
    """
    return db_util.query(Project, owner_id=owner_id)


def get_project_by_id(owner_id, project_id):
    """
    根据owner_id和project_name获得工程
    :param owner_id:
    :param project_id:
    :return:
    """
    return db_util.query(Project, owner_id=owner_id, project_id=project_id)


def delete_project(project_id):
    update = {'is_deleted': 1}
    num = db_util.update_dict(Project, update, id=project_id)

    return True if num > 0 else False


def update_project(project):
    """
    更新工程相关信息
    :param project: 数据库映射对象
    :return: 更新的条目数
    """
    update = db_util.update_transform(Project, project)
    return db_util.update_dict(Project, update, id=project.id)
