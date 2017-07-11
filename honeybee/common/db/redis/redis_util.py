import redis
from rest.app_base import app
import logging as log
from beans.model_lifecycle import LifeCycle
import cPickle as pickle


class RedisDBConfig:
    HOST = app.config['REDIS_HOST']
    PORT = app.config['REDIS_PORT']
    DBID = app.config['REDIS_DB_ID']


def operator_status(func):
    """
    get operatoration status
    """

    def gen_status(*args, **kwargs):
        error, result = None, None
        try:
            result = func(*args, **kwargs)
        except Exception as e:
            error = str(e)
            log.error(error)

        return result
        # return {'result': result, 'error': error}

    return gen_status


class RedisCache(object):
    def __init__(self):
        if not hasattr(RedisCache, 'pool'):
            RedisCache.create_pool()
        self._connection = redis.Redis(connection_pool=RedisCache.pool)

    @staticmethod
    def create_pool():
        RedisCache.pool = redis.ConnectionPool(
            host=RedisDBConfig.HOST,
            port=RedisDBConfig.PORT,
            db=RedisDBConfig.DBID)

    @operator_status
    def set_data(self, key, value):
        """
        set data with (key, value)
        """
        return self._connection.set(key, value)

    @operator_status
    def get_data(self, key):
        """
        get data by key
        """
        return self._connection.get(key)

    @operator_status
    def del_data(self, key):
        """
        delete cache by key
        """
        return self._connection.delete(key)


def get(key):
    return RedisCache().get_data(key)


def set(key, value):
    return RedisCache().set_data(key, value)


def delete(key):
    return RedisCache().del_data(key)


def dump(key, value):
    set(key, pickle.dumps(value))


def load(key):
    pickle.loads(get(key))
