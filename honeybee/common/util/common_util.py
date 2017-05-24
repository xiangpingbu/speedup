# coding=utf-8
import os


def listFile(rootdir,absolute = True):
    """
    列出rootdir下的所有文件
    :param rootdir: 根路径
    :param absolute: 是否展示绝对路径,如果为False,那么展示基于rootdir的相对路径
    :return: list of file path
    """
    list = []
    __list(rootdir,list)

    if absolute is False:
        list = map(lambda x:x.replace(rootdir+"/",""),list)

    return list

def __list(rootdir,list):
    """
    listFile的字方法,用于递归调用
    :param rootdir: 根路径
    :param list: list of file path
    :return:
    """
    for root, dirs, files in os.walk(rootdir):  # 三个参数：分别返回1.父目录 2.所有文件夹名字（不含路径） 3.所有文件名字
        for dirname in dirs:
            __list(dirname,list)

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



