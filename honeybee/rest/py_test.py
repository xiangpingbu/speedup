# coding=utf-8

from rest.app_base import *
import copy
import xml.dom.minidom
from io import BytesIO
from common import global_value

datas = [{'name': 'javascript', 'useto': 'web development'},
         {'name': 'python', 'useto': 'do anything'},
         {'name': 'php', 'useto': 'web development'},
         {'name': 'c++', 'useto': 'web server'}]


@app.route('/languages')
def getAll():
    return fullResponse(R200_OK, datas)


@app.route('/tool', methods=['GET', 'POST'])
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
        index = binNums.encode('utf8').split(',')
        for i in range(int(index[0]), int(index[1])):
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

    nameE = dom.createElement('name')
    nameT = dom.createTextNode('linux')
    nameE.appendChild(nameT)
    employee.appendChild(nameE)

    ageE = dom.createElement('age')
    ageT = dom.createTextNode('30')
    ageE.appendChild(ageT)
    employee.appendChild(ageE)

    output = BytesIO()
    dom.writexml(output, addindent='  ', newl='\n', encoding='utf-8')
    s = output.getvalue()
    print s
    # f.close()
    # print s


def parseXml():
    s = '<?xml version="1.0" encoding="utf-8"?><employees><employee><name>linux</name><age>30</age></employee></employees>'
    doc = xml.dom.minidom.parseString(s)
    output = BytesIO()
    xml_str = doc.toprettyxml(indent="  ", newl="\n", encoding='utf-8')
    # repl = lambda x: ">%s</" % x.group(1).strip() if len(x.group(1).strip()) != 0 else x.group(0)
    # pretty_str = re.sub(r'>\n\s*([^<]+)</', repl, xml_str)
    print xml_str


def ppp():
    os.ex
    return global_value.get_value("")


jar_path  = app.config["JAR_PATH"]
target = 'apply'
origin = '/Users/lifeng/Desktop/111/model_data.xlsx'
applied = '/Users/lifeng/Downloads/df_iv_1.xlsx'
columnConfig = '{"data":{"手机入网时间":[{"手机入网时间":["5年以上","nan"],"woe":"-0.2903","binNum":"0","type":"Categorical"},{"手机入网时间":["3--5年"],"woe":"0.0267","binNum":"1","type":"Categorical"},{"手机入网时间":["1--3年"],"woe":"0.1479","binNum":"2","type":"Categorical"},{"手机入网时间":["6-12个月"],"woe":"0.2521","binNum":"3","type":"Categorical"}],"年龄":[{"max":"22.00000","min":"18.00000","min_boundary":"0","max_boundary":"23.00000","woe":"-0.1085","binNum":"1","type":"Numerical"},{"max":"35.00000","min":"23.00000","min_boundary":"23.00000","max_boundary":"36.00000","woe":"0.0075","binNum":"2","type":"Numerical"},{"max":"42.00000","min":"36.00000","min_boundary":"36.00000","max_boundary":"43.00000","woe":"0.0528","binNum":"3","type":"Numerical"},{"max":"54.00000","min":"43.00000","min_boundary":"43.00000","max_boundary":"inf","woe":"-0.1373","binNum":"4","type":"Numerical"}],"性别":[{"性别":["女"],"woe":"-0.3294","binNum":"0","type":"Categorical"},{"性别":["男"],"woe":"0.1026","binNum":"1","type":"Categorical"}],"工作年限":[{"max":"1.00000","min":"1.00000","min_boundary":"0","max_boundary":"2.00000","woe":"-0.1525","binNum":"1","type":"Numerical"},{"max":"2.00000","min":"2.00000","min_boundary":"2.00000","max_boundary":"3.00000","woe":"-0.1979","binNum":"2","type":"Numerical"},{"max":"8.00000","min":"3.00000","min_boundary":"3.00000","max_boundary":"9.00000","woe":"-0.0889","binNum":"3","type":"Numerical"},{"max":"13.00000","min":"9.00000","min_boundary":"9.00000","max_boundary":"14.00000","woe":"-0.1887","binNum":"4","type":"Numerical"},{"max":"15.00000","min":"14.00000","min_boundary":"14.00000","max_boundary":"inf","woe":"0.4333","binNum":"5","type":"Numerical"}],"公司性质":[{"公司性质":["国有股份","机关及事业单位","社会团体"],"woe":"-0.4807","binNum":"0","type":"Categorical"},{"公司性质":["个体"],"woe":"-0.1612","binNum":"1","type":"Categorical"},{"公司性质":["nan","民营","外资"],"woe":"-0.067","binNum":"2","type":"Categorical"},{"公司性质":["私营","合资"],"woe":"0.6192","binNum":"3","type":"Categorical"}]},"target":"bad_4w","modelName":"model_data","branch":"master"}'
# s = os.system("java -jar maas-offline.jar )
cmd = 'java -jar %s %s "origin=%s&applied=%s&columnConfig=%s"'
cmd = cmd %(jar_path,target,origin,applied,columnConfig)
import subprocess
# p2 = subprocess.getoutput(cmd)
# p2 =subprocess.check_output(cmd, shell=True)
# print p2
