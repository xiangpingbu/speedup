# coding=utf-8
import copy

from flask import request
from rest import py_test

import rest.endpoint_tool
import rest.endpoint_es
import rest.endpoint_tool_db
from rest.app_base import *


if __name__ == '__main__':
    app.run(host='0.0.0.0',port=8091,debug=True,use_reloader=False,threaded=True)

