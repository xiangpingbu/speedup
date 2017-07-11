# coding=utf-8
import logging as log

from common.db.mysql import db_util
from beans.tool_model import Variable, Source
from datetime import datetime
from common.constant import DBConstant


def save_variables(variables):
    """
    保存多个变量
    :param variables: variable list
    :return: 主键 list
    """
    return db_util.add_all(variables)


def get_selected_variables(source_id):
    """
    获得被选中的变量,包括target
    :param source_id: 资源id
    :return:
    {   'used': selected_variable
        'target': target_variable
    }
    """
    variables = get_variables(source_id)
    used = list()
    selected = dict(used=used)
    for variable in variables:
        if variable.usage == DBConstant.VARIABLE_USED:
            used.append(variable)
        elif variable.usage == DBConstant.VARIABLE_TARGET:
            selected['target'] = variable
    return selected


def get_variables(source_id):
    """
    获取某个数据源解析完成的概览数据
    :param source_id:
    :return:
    """
    return db_util.query(Variable, source_id=source_id, is_deleted=0)


def set_variable_selected(source_id, selected_variable_list, target):
    """
    选择要使用的变量,还有训练数据需要用到的target
    :param source_id: 所属数据集的id
    :param selected_variable_list: 选择的变量名的列表
    :param target: 训练时的决定数据好坏的变量
    :return: None
    """
    # 直接获取会话,在该会话中执行所有的操作
    session = db_util.get_orm().session
    try:
        now = datetime.now()
        query = session.query(Variable).filter(Variable.source_id == source_id)
        update = dict(modify_date=now)

        # 说明该资源已经可用了
        session.query(Source).filter(Source.id == source_id).update(
            {'modify_date': now, 'available': DBConstant.SOURCE_AVAILABLE})

        for variable in query:
            name = variable.variable_name
            # 更新被选的变量
            if variable.variable_name in selected_variable_list:
                update['usage'] = DBConstant.VARIABLE_USED
                query.filter(Variable.variable_name == name).update(update)
            else:
                # 更新未被选的变量
                update['usage'] = DBConstant.VARIABLE_UNUSED
                query.filter(Variable.variable_name == name).update(update)
        # 更新target
        update['usage'] = DBConstant.VARIABLE_TARGET
        query.filter(Variable.variable_name == target).update(update)
    except Exception, e:
        log.error(e, exc_info=1)
        session.rollback()
        raise e
    finally:
        session.commit()
        session.close()
