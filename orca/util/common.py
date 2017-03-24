# coding=utf-8
def transfer(str):
    """将字符串转化为utf8"""
    return str.encode('utf-8')

# def transferNan(str):

def replace(str):
    """将字符串中的|替换为,"""
    return str.replace("|",",")