# coding=utf-8

from common.db_orm.sql_session import get_orm


def query(table, *criterion,**params):
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
            raise Exception(e)

# def query(table, **criterion):
#     """
#     查询单条或者多条数据
#     :param table:  数据库表映射对象
#     :param criterion:   查询条件
#     :return:  查询得到的一条或者多条数据
#     """
#     with get_orm() as s:
#         try:
#             condition = map(lambda x: table.__dict__[x] == criterion[x], criterion)
#             result = s.session.query(table).filter(*condition)
#             return result
#         except Exception, e:
#             s.session.rollback()
#             raise Exception(e)


def add_all(*tables):
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
                ids = map(lambda x: x, tables)
        except Exception, e:
                s.session.rollback()
                raise Exception(e)
        s.session.commit()
        return ids


def update_dict(table, update, **criterion):
    """
    更新指定的数据库表
    :param table:  数据库表隐射对象
    :param update: 要更新的字段
    :param criterion: 获取数据的条件
    :return: 更新的数据的条目数
    """
    with get_orm() as s:
        try:
            condition = map(lambda x:table.__dict__[x]==criterion[x],criterion)
            result = s.session.query(table).filter(*condition).update(update)
            s.session.flush()
        except Exception,e:
            s.session.rollback()
            raise Exception(e)
        s.session.commit()
        return result


