# coding=utf-8
import os

'''
多环境变量配置,默认的情况下会使用Development中的配置
如果将FLASK_ENV设置为环境变量,且设置值为PRODUCTION,那么将会启用Production的配置
'''

class Config(object):
    DEBUG = False
    BASEDIR = os.path.abspath(os.path.dirname(__file__))
    ROOT_PATH="/Users/lifeng/Desktop/111"
    ES_HOST = "http://10.10.10.107:9200/"
    JAR_PATH="/Users/lifeng/Work/Code/maas-fork/maas/maas-offline/target/maas-offline.jar"
    REDIS_HOST = '127.0.0.1'
    REDIS_PORT = 6379
    REDIS_DB_ID = 0



class Development(Config):  # inherit from Config
    DEBUG = True


class Production(Config):
    DEBUG = False
    HOST = '127.0.0.1'
    ROOT_PATH = ""
    ES_HOST = "http://10.10.10.107:9200/"
