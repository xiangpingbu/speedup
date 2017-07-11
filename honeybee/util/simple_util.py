# coding=utf-8
import os
import hashlib
from decimal import Decimal


def listFile(rootdir, absolute=True):
    """
    列出rootdir下的所有文件
    :param rootdir: 根路径
    :param absolute: 是否展示绝对路径,如果为False,那么展示基于rootdir的相对路径
    :return: list of file path
    """
    list = []
    __list(rootdir, list)

    if absolute is False:
        list = map(lambda x: x.replace(rootdir + "/", ""), list)

    return list


def __list(rootdir, list):
    """
    listFile的字方法,用于递归调用
    :param rootdir: 根路径
    :param list: list of file path
    :return:
    """
    for root, dirs, files in os.walk(rootdir):  # 三个参数：分别返回1.父目录 2.所有文件夹名字（不含路径） 3.所有文件名字
        for dirname in dirs:
            __list(dirname, list)

        for filename in files:
            list.append(os.path.join(root, filename))


def is_num(v):
    """
    判断v是否为数字
    :param v:
    :return:
    """
    try:
        val = float(v)
        return True
    except ValueError:
        return False


def float_nan_to_str_nan(x):
    """
    float类型的nan转换为字符串
    :param x:
    :return:
    """
    if type(x) == float:
        return str(x)
    else:
        return x


def to_md5(str):
    """
    md5加密
    :param str: 被加密的字符串
    :return:  密文
    """
    m = hashlib.md5()
    m.update(str)
    s = m.hexdigest()
    return s


def get_file_size(file_path):
    byte_size = os.path.getsize(file_path)
    unit = 1024

    if byte_size < unit:
        return byte_size + 'B'
    elif (byte_size >> 10) < unit :
        return str(round(Decimal(byte_size / 1024.0), 1)) + 'K'
    elif (byte_size >> 20) < unit:
        return str(round(Decimal(byte_size / 1024.0 / 1024.0), 1)) + 'M'
    else :
        return str(round(Decimal(byte_size / 1024.0 / 1024.0/1024.0), 1))+'G'



