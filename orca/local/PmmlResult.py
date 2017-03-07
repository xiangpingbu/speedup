# coding=utf-8
import json


class Pmml():
    def __init__(self):
        """内部初始化"""
        self.columnNum = "2"
        self.columnName = ""
        self.version = ""
        # 如果是数字即为N,如果为字符即为S
        self.columnType = ""
        self.columnFlag = None
        self.finalSelect = True
        self.columnStats = {"max":3.0,"min":1.0,"mean":2.1363636363636362,"median":2.0,
                            "totalCount":80,"distinctCount":None,"missingCount":2,"stdDev":0.7208192803782614,
                            "missingPercentage":0.025,"woe":0.4946962418345462,"ks":6.926829268292681,"iv":0.0567787375909018,
                            "weightedKs":6.926829268292681,"weightedIv":0.0567787375909018,"weightedWoe":0.4946962418345462,
                            "skewness":None,"kurtosis":None}
        self.columnBinning = {"length": None, "binCategory": [], "binBoundary": [], "binCountWoe": [],
                              "binCountNeg":[],"binCountPos":[],"binPosRate":[],"binAvgScore":None,
                              "binWeightedNeg":[],"binWeightedPos":[],"binWeightedWoe":[]}


credit_query_times_three = '{"columnType": "N",' \
                           '"columnFlag": null, ' \
                           '"finalSelect": true, ' \
                           '"columnNum": "1", ' \
                           '"columnName": "credit_query_times_three", ' \
                           '"columnBinning": {"binCategory":null,"binCountWoe": [0,0.3289,0.0939,-0.1082,-0.3609,-0.7697,-0.1245],' \
                           '"binBoundary": ["-Infinity",-999998,1,5,6,9]}, ' \
                           '"version": "", ' \
                           '"columnStats":{"max":3.0,"min":1.0,"mean":2.1363636363636362,"median":2.0,' \
                           '"totalCount":80,"distinctCount":null,"missingCount":2,"stdDev":0.7208192803782614,' \
                           '"missingPercentage":0.025,"woe":0.4946962418345462,"ks":6.926829268292681,"iv":0.0567787375909018,' \
                           '"weightedKs":6.926829268292681,"weightedIv":0.0567787375909018,"weightedWoe":0.4946962418345462,"skewness":null,' \
                           '"kurtosis":null}}'

credit_c_credit_amount_sum = '{"columnType": "N",' \
                             '"columnFlag": null, ' \
                             '"finalSelect": true, ' \
                             '"columnNum": "2", ' \
                             '"columnName": "credit_c_credit_amount_sum", ' \
                             '"columnBinning": {"binCategory":null,"binCountWoe": [0,-0.5007,-0.1528,0.0161,0.2159,0.4574,-0.1309],' \
                             '"binBoundary": ["-Infinity",-999998,6100,48598,156500,232000]}, ' \
                             '"version": "", ' \
                             '"columnStats":{"max":3.0,"min":1.0,"mean":2.1363636363636362,"median":2.0,' \
                             '"totalCount":80,"distinctCount":null,"missingCount":2,"stdDev":0.7208192803782614,' \
                             '"missingPercentage":0.025,"woe":0.4946962418345462,"ks":6.926829268292681,"iv":0.0567787375909018,' \
                             '"weightedKs":6.926829268292681,"weightedIv":0.0567787375909018,"weightedWoe":0.4946962418345462,"skewness":null,' \
                             '"kurtosis":null}}'

credit_utilization = '{"columnType": "N",' \
                     '"columnFlag": null, ' \
                     '"finalSelect": true, ' \
                     '"columnNum": "3", ' \
                     '"columnName": "credit_utilization", ' \
                     '"columnBinning": {' \
                     '"binCategory":null,' \
                     '"binCountWoe": [0,0.1421,0.0329,-0.1524,-0.3616,-0.1496],' \
                     '"binBoundary": ["-Infinity",-999998,0.249813,0.6888235,0.8948689,0.9859697]}, ' \
                     '"version": "", ' \
                     '"columnStats":{"max":3.0,"min":1.0,"mean":2.1363636363636362,"median":2.0,' \
                     '"totalCount":80,"distinctCount":null,"missingCount":2,"stdDev":0.7208192803782614,' \
                     '"missingPercentage":0.025,"woe":0.4946962418345462,"ks":6.926829268292681,"iv":0.0567787375909018,' \
                     '"weightedKs":6.926829268292681,"weightedIv":0.0567787375909018,"weightedWoe":0.4946962418345462,"skewness":null,' \
                     '"kurtosis":null}}'

# '"binCategory":["1","2","3","4","missing","invalid"],' \
personal_education = '{"columnType": "C",' \
                     '"columnFlag": null, ' \
                     '"finalSelect": true, ' \
                     '"columnNum": "4", ' \
                     '"columnName": "personal_education", ' \
                     '"columnBinning": {' \
                     '"binCategory":["硕士及以上","本科","专科","其他","missing","invalid"],' \
                     '"binCountWoe": [0.5664,0.2364,-0.0886,-0.3337,0,0],' \
                     '"binBoundary": null}, ' \
                     '"version": "", ' \
                     '"columnStats":{"max":3.0,"min":1.0,"mean":2.1363636363636362,"median":2.0,' \
                     '"totalCount":80,"distinctCount":null,"missingCount":2,"stdDev":0.7208192803782614,' \
                     '"missingPercentage":0.025,"woe":0.4946962418345462,"ks":6.926829268292681,"iv":0.0567787375909018,' \
                     '"weightedKs":6.926829268292681,"weightedIv":0.0567787375909018,"weightedWoe":0.4946962418345462,"skewness":null,' \
                     '"kurtosis":null}}'

personal_live_case = '{"columnType": "C",' \
                     '"columnFlag": null, ' \
                     '"finalSelect": true, ' \
                     '"columnNum": "5", ' \
                     '"columnName": "personal_live_case", ' \
                     '"columnBinning": {' \
                     '"binCategory":["自有商业按揭房","自有无按揭购房","自有公积金按揭购房","自建房","房","亲戚住房","宿舍","其他","missing","invalid"],' \
                     '"binCountWoe": [0.0297,0.1522,0.3626,-0.2545,-0.0559,-0.0559,-0.1686,-0.1686,0,0],' \
                     '"binBoundary": null}, ' \
                     '"version": "", ' \
                     '"columnStats":{"max":3.0,"min":1.0,"mean":2.1363636363636362,"median":2.0,' \
                     '"totalCount":80,"distinctCount":null,"missingCount":2,"stdDev":0.7208192803782614,' \
                     '"missingPercentage":0.025,"woe":0.4946962418345462,"ks":6.926829268292681,"iv":0.0567787375909018,' \
                     '"weightedKs":6.926829268292681,"weightedIv":0.0567787375909018,"weightedWoe":0.4946962418345462,"skewness":null,' \
                     '"kurtosis":null}}'

client_gender = '{"columnType": "C",' \
                '"columnFlag": null, ' \
                '"finalSelect": true, ' \
                '"columnNum": "6", ' \
                '"columnName": "client_gender", ' \
                '"columnBinning": {' \
                '"binCategory":["男","女","missing","invalid"],' \
                '"binCountWoe": [-0.1056,0.276,-0.0439,0],' \
                '"binBoundary": null}, ' \
                '"version": "", ' \
                '"columnStats":{"max":3.0,"min":1.0,"mean":2.1363636363636362,"median":2.0,' \
                '"totalCount":80,"distinctCount":null,"missingCount":2,"stdDev":0.7208192803782614,' \
                '"missingPercentage":0.025,"woe":0.4946962418345462,"ks":6.926829268292681,"iv":0.0567787375909018,' \
                '"weightedKs":6.926829268292681,"weightedIv":0.0567787375909018,"weightedWoe":0.4946962418345462,"skewness":null,' \
                '"kurtosis":null}}'

personal_live_join = '{"columnType": "C",' \
                     '"columnFlag": null, ' \
                     '"finalSelect": true, ' \
                     '"columnNum": "7", ' \
                     '"columnName": "personal_live_join", ' \
                     '"columnBinning": {' \
                     '"binCategory":["父母","配偶及子女","父母&配偶及子女","其他","朋友","配偶及子女&其他","父母,其他","父母&配偶及子女&其他","missing","invalid"],' \
                     '"binCountWoe": [0.0313,0.0313,0.0313,-0.2019,-0.1069,-0.1069,-0.1069,-0.1069,0,0],' \
                     '"binBoundary": null}, ' \
                     '"version": "", ' \
                     '"columnStats":{"max":3.0,"min":1.0,"mean":2.1363636363636362,"median":2.0,' \
                     '"totalCount":80,"distinctCount":null,"missingCount":2,"stdDev":0.7208192803782614,' \
                     '"missingPercentage":0.025,"woe":0.4946962418345462,"ks":6.926829268292681,"iv":0.0567787375909018,' \
                     '"weightedKs":6.926829268292681,"weightedIv":0.0567787375909018,"weightedWoe":0.4946962418345462,"skewness":null,' \
                     '"kurtosis":null}}'

personal_year_income = '{"columnType": "N",' \
                       '"columnFlag": null, ' \
                       '"finalSelect": true, ' \
                       '"columnNum": "8", ' \
                       '"columnName": "personal_year_income", ' \
                       '"columnBinning": {' \
                       '"binCategory":null,' \
                       '"binCountWoe": [0,0.1063,-0.0362,-0.0853,0.4706,0.6784],' \
                       '"binBoundary": ["-Infinity",-999998,11.68,18,50]}, ' \
                       '"version": "", ' \
                       '"columnStats":{"max":3.0,"min":1.0,"mean":2.1363636363636362,"median":2.0,' \
                       '"totalCount":80,"distinctCount":null,"missingCount":2,"stdDev":0.7208192803782614,' \
                       '"missingPercentage":0.025,"woe":0.4946962418345462,"ks":6.926829268292681,"iv":0.0567787375909018,' \
                       '"weightedKs":6.926829268292681,"weightedIv":0.0567787375909018,"weightedWoe":0.4946962418345462,"skewness":null,' \
                       '"kurtosis":null}}'

loan_repayment_frequency_avg = '{"columnType": "N",' \
                               '"columnFlag": null, ' \
                               '"finalSelect": true, ' \
                               '"columnNum": "9", ' \
                               '"columnName": "loan_repayment_frequency_avg", ' \
                               '"columnBinning": {' \
                               '"binCategory":null,' \
                               '"binCountWoe": [0,0.495,-0.179,0.0214],' \
                               '"binBoundary": ["-Infinity",-999998,0.9]}, ' \
                               '"version": "", ' \
                               '"columnStats":{"max":3.0,"min":1.0,"mean":2.1363636363636362,"median":2.0,' \
                               '"totalCount":80,"distinctCount":null,"missingCount":2,"stdDev":0.7208192803782614,' \
                               '"missingPercentage":0.025,"woe":0.4946962418345462,"ks":6.926829268292681,"iv":0.0567787375909018,' \
                               '"weightedKs":6.926829268292681,"weightedIv":0.0567787375909018,"weightedWoe":0.4946962418345462,"skewness":null,' \
                               '"kurtosis":null}}'
age = '{"columnType": "N",' \
      '"columnFlag": null, ' \
      '"finalSelect": true, ' \
      '"columnNum": "10", ' \
      '"columnName": "age", ' \
      '"columnBinning": {' \
      '"binCategory":null,' \
      '"binCountWoe": [0,-0.2699,-0.1819,0.0129,0.0263,0.1775,0.2687,-0.0439],' \
      '"binBoundary": ["-Infinity",-999998,28,30,45,50,54]}, ' \
      '"version": "", ' \
      '"columnStats":{"max":3.0,"min":1.0,"mean":2.1363636363636362,"median":2.0,' \
      '"totalCount":80,"distinctCount":null,"missingCount":2,"stdDev":0.7208192803782614,' \
      '"missingPercentage":0.025,"woe":0.4946962418345462,"ks":6.926829268292681,"iv":0.0567787375909018,' \
      '"weightedKs":6.926829268292681,"weightedIv":0.0567787375909018,"weightedWoe":0.4946962418345462,"skewness":null,' \
      '"kurtosis":null}}'

variableList = [credit_query_times_three,
                credit_c_credit_amount_sum,
                credit_utilization,
                personal_education,
                personal_live_case,
                client_gender,
                personal_live_join,
                personal_year_income,
                loan_repayment_frequency_avg,
                age]

list = []
# print client_gender

for v in variableList:
    dict = json.loads(v)
    pmml = Pmml()
    pmml.__dict__ = dict
    if pmml.columnBinning["binCategory"] is not None and len(pmml.columnBinning["binCategory"]) != 0:
        pmml.columnBinning["length"] = len(pmml.columnBinning["binCategory"])
    elif pmml.columnBinning["binBoundary"] is not None and len(pmml.columnBinning["binBoundary"]) != 0:
        pmml.columnBinning["length"] = len(pmml.columnBinning["binBoundary"])
    length=pmml.columnBinning["length"]
    pmml.columnBinning["binCountNeg"]=[]
    pmml.columnBinning["binCountPos"]=[]
    pmml.columnBinning["binPosRate"]=[]
    pmml.columnBinning["binAvgScore"]=[]
    pmml.columnBinning["binWeightedNeg"]=[]
    pmml.columnBinning["binWeightedPos"]=[]
    pmml.columnBinning["binWeightedWoe"]=[]

    for i in range(1,length):
        pmml.columnBinning["binCountNeg"].append(1)
        pmml.columnBinning["binCountPos"].append(2)
    list.append(pmml.__dict__)



str = json.dumps(list, ensure_ascii=False)

print str
file_object = open('/Users/lifeng/Desktop/ColumnConfig.json', 'w')
file_object.write(str.encode("utf-8"))
file_object.close()
