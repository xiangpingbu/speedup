# coding=utf-8
from common.db.mysql.sql_session import get_orm
import logging
from sqlalchemy.exc import IntegrityError
from common.exceptions import HoneybeeException
from datetime import datetime


def query(table, *criterion, **params):
    """
    查询单条或者多条数据
    :param table:  数据库表映射对象
    :param criterion:   查询条件
    :return:  查询得到的一条或者多条数据
    """
    with get_orm() as s:
        try:
            condition = map(lambda x: table.__dict__[x] == params[x], params)
            condition.extend(criterion)
            result = s.session.query(table).filter(*condition)
            return result
        except Exception, e:
            s.session.rollback()
            raise e


def add_one(table):
    """
    插入一条或者多条数据
    :param tables: 数据库表映射对象
    :return: 插入的一条或者多条数据的主键,以list形式返回
    """
    with get_orm() as s:
        try:
            s.session.add(table)
            s.session.flush()
            id = table.id
            return id
        except Exception, e:
            s.session.rollback()
            raise e
        finally:
            s.session.commit()


def add_all(tables):
    """
    插入一条或者多条数据
    :param tables: 数据库表映射对象
    :return: 插入的一条或者多条数据的主键,以list形式返回
    """
    with get_orm() as s:
        try:
            for table in tables:
                s.session.add(table)
            s.session.flush()
            ids = map(lambda x: x.id, tables)
            return ids
        except Exception, e:
            s.session.rollback()
            raise e
        finally:
            s.session.commit()


def update_dict(table, update, **criterion):
    """
    更新指定的数据库表,可以更新一条或者多条数据
    取决于criterion能否帮助filter得到多条数据
    :param table:  数据库表隐射对象
    :param update: 要更新的字段
    :param criterion: 获取数据的条件
    :return: 更新的数据的条目数
    """
    with get_orm() as s:
        try:
            update['modify_date'] = datetime.now()
            condition = map(lambda x: table.__dict__[x] == criterion[x], criterion)
            result = s.session.query(table).filter(*condition).update(update)
            s.session.flush()
        except Exception, e:
            s.session.rollback()
            raise Exception(e)
        s.session.commit()
        return result


def update_transform(table, instance):
    """
    将数据库映射对象转换为一个字典
    :param table: 数据库映射类
    :param instance: 数据库映射对象
    :return: 需要被更新的字段
    """
    update = {}
    for key, value in instance.__dict__.items():
        a = key.find("_", 0, 1)
        if a != 0:
            update[table.__dict__[key]] = value
    return update


def error_handler(func):
    """
    get operatoration status
    """

    def gen_status(*args, **kwargs):
        error, result = None, None
        try:
            result = func(*args, **kwargs)
        except Exception as e:
            if func.func_defaults != None:
                result = func.func_defaults[-1]
            if isinstance(e, HoneybeeException):
                raise e
            elif isinstance(e, IntegrityError):
                error = 'duplicate record'
            else:
                error = 'error while operate database'
            logging.error(error, exc_info=1)

        return {'result': result, 'error': error}

    return gen_status
