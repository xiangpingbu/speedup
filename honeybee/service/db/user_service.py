# coding=utf-8
import logging as log

from sqlalchemy.exc import IntegrityError

from beans.tool_model import User
from common.db.mysql import db_util
from common.exceptions import *
from util import simple_util as cmm


def auth_user(email, password):
    """
    验证用户的邮箱和密码是否一致
    :param default: 出异常时默认的返回值
    :param email: 邮箱
    :param password: 密码
    :return: 是否一致
    """
    user = find_user(email).get("result")
    if user is not None:
        if user.user_password != cmm.to_md5(password):
            raise Error.USER_PASSWORD_ERROR
        else:
            return True
    else:
        raise Error.USER_PASSWORD_ERROR


def add_user(user):
    """
    添加用户
    :param user: 用户对象
    :return: 用户是否添加
    """
    try:
        result = db_util.add_one(user)
    except IntegrityError, e:
        log.warn("user duplicate while add user:%s", user.user_email, exc_info=1)
        raise Error.USER_EXIST
    return result


def find_user(email):
    """
    通过邮箱查找用户
    :param email: 邮箱
    :return: 用户信息
    """
    results = db_util.query(User, user_email = email,is_deleted = 0)
    return results.first()
