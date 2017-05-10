# coding=utf-8
class const:
    class ConstError(TypeError):
        pass
    class ConstCaseError(ConstError):
        pass

'''
覆盖变量赋值方法,如果监控到常量已经被修改,那么抛出异常
'''
def __setattr__(self, key, value):
    if self.__dict__.has_key(key):
        raise self.ConstError,"constant reassignment error!"
    self.__dict__[key] = value

import sys

sys.modules[__name__] = const()

