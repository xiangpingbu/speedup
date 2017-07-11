# coding=utf-8
import logging as log

from common.db.mysql import db_util
from beans.tool_model import Source, Variable
from  datetime import datetime

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


def get_source(id):
    """
    根据主键获得source
    :param id: 主键
    :return: source
    """
    return db_util.query(Source, is_deleted=0, id=id)


def get_sources(project_id):
    """
    获得用户的所有资源
    :param project_id: 工程id
    :return: list of source
    """
    return db_util.query(Source, is_deleted=0, project_id=project_id)


def delete_source_by_id(source_id):
    """
    删除资源
    :param source_id: 资源相关主键id
    :return: 数据库更新的条目数
    """
    session = db_util.get_orm().session
    try:
        update = {'is_deleted': 1, 'modify_date': datetime.now()}
        session.query(Source).filter(Source.id == source_id).update(update)
        session.query(Variable).filter(Variable.source_id == source_id).update(update)
        session.flush()
    except Exception, e:
        session.rollback()
        log.error(e, exc_info=1)
        raise e
    finally:
        session.commit()
        session.close()


def update_source(source):
    """
    更新资源相关变量
    :param source: 数据库映射对象
    :return: 数据库更新条目数
    """
    update = db_util.update_transform(Source, source)
    return db_util.update_dict(Source, update, id=source.id)


