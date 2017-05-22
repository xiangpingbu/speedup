# coding=utf-8

from rest.app_base import *
import copy
import xml.dom.minidom
from io import BytesIO
import re


datas = [{'name': 'javascript', 'useto': 'web development'},
         {'name': 'python', 'useto': 'do anything'},
         {'name': 'php', 'useto': 'web development'},
         {'name': 'c++', 'useto': 'web server'}]


@app.route('/languages')
def getAll():
    return fullResponse(R200_OK, datas)
@app.route('/tool',methods=['GET','POST'])
def resp():
    if request.method == "POST":
        binNums = request.form.get('binNums')
    else:
        binNums = None
    data = [[{"bin_num": 0, "woe": 0.1564},
            {"bin_num": 1, "woe": 0.0726},
            {"bin_num": 2, "woe": 0.0236},
            {"bin_num": 3, "woe": -0.1455},
            {"bin_num": 4, "woe": -0.1472},
            {"bin_num": 5, "woe": -0.4853},
            {"bin_num": 6, "woe": -0.1629}]]
    data2 = copy.copy(data)
    print data
    if binNums is not None:
        index=binNums.encode('utf8').split(',')
        for i in range(int(index[0]),int(index[1])):
            for d in data:
                if d["bin_num"] == i:
                    data.remove(d)
    print binNums
    return responseto(data=data)


# 根据name获取资源中的某一个
@app.route('/language/<string:name>')
def getOne(name):
    result = [data for data in datas if data['name'] == name]
    if len(result) == 0:
        return statusResponse(R404_NOTFOUND)
    return fullResponse(R200_OK, result[0])


# POST请求
@app.route('/language', methods=['POST'])
def addOne():
    request_data = request.get_json()
    if not 'name' in request_data or not 'useto' in request_data:
        return statusResponse(R400_BADREQUEST)
    name = request_data['name']
    useto = request_data['useto']
    datas.append({'name': name, 'useto': useto})
    return statusResponse(R201_CREATED)


# PUT，PATCH 更新资源
# 按照RestFul设计：
# PUT动作要求客户端提供改变后的完整资源
# PATCH动作要求客户端可以只提供需要被改变的属性
# 在这里统一使用PATCH的方法
@app.route('/language/<string:name>', methods=['PUT', 'PATCH'])
def editOne(name):
    result = [data for data in datas if data['name'] == name]
    if len(result) == 0:
        return statusResponse(R404_NOTFOUND)
    request_data = request.get_json()
    if 'name' in request_data:
        result[0]['name'] = request_data['name']
    if 'useto' in request_data:
        result[0]['useto'] = request_data['useto']
    return statusResponse(R201_CREATED)


# DELETE删除
@app.route('/language/<string:name>', methods=['DELETE'])
def delOne(name):
    result = [data for data in datas if data['name'] == name]
    if len(result) == 0:
        return statusResponse(R404_NOTFOUND)
    datas.remove(result[0])
    return statusResponse(R204_NOCONTENT)


def GenerateXml():

    impl = xml.dom.minidom.getDOMImplementation()
    dom = impl.createDocument(None, 'employees', None)
    root = dom.documentElement
    employee = dom.createElement('employee')
    root.appendChild(employee)

    nameE=dom.createElement('name')
    nameT=dom.createTextNode('linux')
    nameE.appendChild(nameT)
    employee.appendChild(nameE)

    ageE=dom.createElement('age')
    ageT=dom.createTextNode('30')
    ageE.appendChild(ageT)
    employee.appendChild(ageE)

    # s = " " % ()
    # f= open('/Users/lifeng/Desktop/employees2.xml', 'w')
    output = BytesIO()
    dom.writexml(output, addindent='  ', newl='\n',encoding='utf-8')
    s = output.getvalue()
    print s
    # f.close()
    # print s

def parseXml():
    s = '<?xml version="1.0" encoding="utf-8"?><employees><employee><name>linux</name><age>30</age></employee></employees>'
    doc = xml.dom.minidom.parseString(s)
    # root = dom.documentElement
    output = BytesIO()
    xml_str  = doc.toprettyxml(indent="  ", newl="\n",encoding='utf-8')
    # repl = lambda x: ">%s</" % x.group(1).strip() if len(x.group(1).strip()) != 0 else x.group(0)
    # pretty_str = re.sub(r'>\n\s*([^<]+)</', repl, xml_str)
    print xml_str



