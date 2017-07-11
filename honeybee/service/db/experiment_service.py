# coding=utf-8


from beans.tool_model import Project
from common.db.mysql import db_util
from beans.tool_model import Experiment



def create_experiment(experiment):
    """
    创建一个新的算法
    :param experiment: 算法信息
    :return:
    """
    return db_util.add_one(experiment)


def get_experiments(project_id):
    """
    获得属于某个工程的所有算法实验
    :param project_id: 工程id
    :return:
    """
    return db_util.query(Experiment, project_id=project_id,is_deleted = 0)


def get_experiment(id):
    """
    获得某个工程的详情
    :param id: 工程id
    :return:
    """
    return db_util.query(Experiment, id=id,is_deleted = 0)


