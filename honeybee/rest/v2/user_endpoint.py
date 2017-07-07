# coding=utf-8
from flask import request

import util.restful_tools as rest
from beans import tool_model
from common.exceptions import Error
from rest.app_base import app
from service.db import user_service
from util import simple_util as cmm


@app.route("/user/login", methods=['post'])
def login():
    """
    用户登录
    :return: 邮箱和密码是否一致
    """
    email = request.form.get('email')
    password = request.form.get('password')
    result = user_service.auth_user(email, password)
    return rest.responseto(result)


@app.route("/user/register", methods=['post'])
def user_register():
    """
    用户注册接口
    :param email:用户邮箱
    :param password: 用户密码
    :param nick_name: 用户昵称
    :return: 注册是否成功
    """
    email = request.form.get("email")
    password = request.form.get("password")
    nick_name = request.form.get("nick_name")

    if email is None or password is None:
        raise Error.USER_LACK_NECESSARY_INFO

    user = tool_model.User()
    user.user_email = email
    # 将密码进行MD5加密
    user.user_password = cmm.to_md5(password)
    user.user_nick = nick_name

    result = user_service.add_user(user)
    if result is None:
        result = False
    else:
        result = True

    return rest.responseto(result)
