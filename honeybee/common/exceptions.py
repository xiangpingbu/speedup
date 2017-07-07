# coding=utf-8
from enum import Enum


class HoneybeeException(Exception):
    def __init__(self, code, message):
        self.code = code
        self.message = message


class Error():
    def __init__(self, *keys, **kwargs):
        self.code = None

    USER_PASSWORD_ERROR = HoneybeeException("101", "用户名密码错误")
    USER_NOT_EXIST = HoneybeeException("102", "用户不存在")
    USER_EXIST = HoneybeeException("103", "用户已存在")
    USER_LACK_NECESSARY_INFO = HoneybeeException("104","用户注册必备数据缺失")

    SOURCE_NO_FILE_RECEIVED = HoneybeeException("201","未获取到上传的文件")
    SOURCE_PARSE_FAIL = HoneybeeException("202","解析资源失败")
    SOURCE_UPDATE_FAIL = HoneybeeException("203","资源更新失败")



