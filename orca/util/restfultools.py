#-*- coding: UTF-8 -*-
from flask import jsonify
import json



# define statu_dics here
R200_OK = {'code': 200, 'message': 'OK all right.'}
R201_CREATED = {'code': 201, 'message': 'All created.'}
R204_NOCONTENT = {'code': 204, 'message': 'All deleted.'}
R400_BADREQUEST = {'code': 400, 'message': 'Bad request.'}
R403_FORBIDDEN = {'code': 403, 'message': 'You can not do this.'}
R404_NOTFOUND = {'code': 404, 'message': 'No result matched.'}


def fullResponse(statu_dic, data):
    return jsonify({'status': statu_dic, 'data': data})


def statusResponse(statu_dic):
    return jsonify({'status': statu_dic})


def responseto(data=None,message=None, error=None, **kwargs):
    """ 封装 json 响应"""
    result = kwargs
    result['error'] = error
    result['message'] = message
    result['data'] = data

    # # 如果提供了 data，那么不理任何其他参数，直接响应 data
    # if not data:
    #     # data = kwargs
    #     result['error'] = error
    #     if message:
    #         # 除非显示提供 error 的值，否则默认为 True
    #         # 意思是提供了 message 就代表有 error
    #         result['message'] = message
    #         if error is None:
    #             result['error'] = True
    #     else:
    #         # 除非显示提供 error 的值，否则默认为 False
    #         # 意思是没有提供 message 就代表没有 error
    #         if error is None:
    #             data['error'] = False
    # # if not isinstance(data, dict):
    # #     data = {'error':True, 'message':'data 必须是一个 dict！'}
    resp = jsonify(result)
    # 跨域设置
    resp.headers['Access-Control-Allow-Origin'] = '*'
    return resp