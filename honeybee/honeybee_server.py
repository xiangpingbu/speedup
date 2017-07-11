# coding=utf-8
from rest import *
from rest.v2 import *

from rest import app_base


if __name__ == '__main__':
    app_base.app.run(host='0.0.0.0',port=8091,debug=True,use_reloader=False,threaded=True)

