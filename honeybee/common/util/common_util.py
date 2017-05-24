# coding=utf-8
import os

def listFile(rootdir,absolute = True):
    list = []
    __list(rootdir,list)

    if absolute is False:
        list = map(lambda x:x.replace(rootdir+"/",""),list)

    return list

def __list(rootdir,list):
    for root, dirs, files in os.walk(rootdir):  # 三个参数：分别返回1.父目录 2.所有文件夹名字（不含路径） 3.所有文件名字
        for dirname in dirs:
            __list(dirname,list)

        for filename in files:
            list.append(os.path.join(root, filename))

