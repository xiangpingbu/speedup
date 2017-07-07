# coding=utf-8
from common.db.mysql import db_util
from beans.tool_model import Source

"""
每一个工程依赖的数据源在数据库中的记录的增删改查
"""


def add_source(source):
    """
    创建一个新的资源
    :param project: 工程信息
    :return: 新的工程记录的主键
    """
    return db_util.add_one(source)


def get_sources(id=None, project_id=None):
    """
    获得用户的所有资源
    :param owner_id:  用户的id
    :return: list of source
    """
    params = {}
    if id != None:
        params['id'] = id
    if project_id != None:
        params['project_id'] = project_id
    return db_util.query(Source, **params)


def delete_source_by_id(source_id):
    """
    删除资源
    :param source_id: 资源相关主键id
    :return: 数据库更新的条目数
    """
    update = {'is_deleted': 1}
    return db_util.update_dict(Source, update, id=source_id)


def update_source(source):
    """
    更新资源相关变量
    :param source: 数据库映射对象
    :return: 数据库更新条目数
    """
    update = db_util.update_transform(Source, source)
    return db_util.update_dict(Source, update, id=source.id)
