# coding=utf-8
value = {}

# coding=utf-8
def set_value(**input_value):
    global value
    # 合并两个字典
    value = dict(value, **input_value)


def get_value(modelname_branch):
    return value[modelname_branch]
