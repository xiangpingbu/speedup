# coding=utf-8
import logging as log

from flask import Flask, request, send_from_directory
from flask_restful import Api
from flask_environments import Environments
from common.exceptions import HoneybeeException
from util import restful_tools as rest

app = Flask(__name__)
# FLASK_ENV变量控制honeybee对环境变量的选择,默认为DEVELOPMENT
# os.environ['FLASK_ENV'] = 'PRODUCTION'
env = Environments(app)
env.from_object('conf.config')
api = Api(app)


@app.errorhandler(Exception)
def handle_error(e):
    """
    全局异常拦截
    :param e: 被截获的异常
    :return: 接口抛出异常后直接将这个异常返回
    """
    code = 500
    if isinstance(e, HoneybeeException):
        code = e.code
    log.error(e,exc_info=1)
    return rest.responseto(None, message=e.message, code=code,success=False)
