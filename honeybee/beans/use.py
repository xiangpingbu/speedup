# coding=utf-8
from sqlalchemy import func, or_, not_

from beans.tool_model import Model
from beans.tool_model import ModelContent
from beans import sql_session
from beans.sql_session import get_orm


def query(table, *criterion):
    """
    :param table:  数据库映射对象
    :param criterion:   查询条件
    :return:  查询得到的一条或者多条数据
    """
    with get_orm() as s:
        result = s.session.query(table).filter(*criterion)
        return result


def add_all(*tables):
    with get_orm() as s:
        for table in tables:
            s.session.add(table)
        s.session.flush()
        ids = map(lambda x: x.id, tables)
        s.session.commit()
        return ids


def update(table, update, *criterion):
    with get_orm() as s:
        result = s.session.query(table).filter(*criterion).update(update)
        s.session.commit()
        return result


def get_session():
    return get_orm().session


models = query(Model, Model.id == '1023')
for model in models:
    print model.id

# results = query(ModelContent,ModelContent.model_name == 'model_train_selected')
# for result in results:
#     print result.variable_name

# dict = {Model.model_branch:'unknow',Model.model_name:'222'}
# print  update(Model,dict,Model.id=='888')
