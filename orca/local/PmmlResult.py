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
        self.columnStats = {}
        self.columnBinning = {"binBoundary":[],"binCountWoe":[]}


pmml = Pmml()
pmml.columnStats['c'] = "阿萨德"
pmmlDict = pmml.__dict__
pmmlJson = json.dumps(pmmlDict, ensure_ascii=False)

json='{"columnType": "N",' \
     '"columnFlag": null, ' \
     '"finalSelect": true, ' \
     '"columnNum": "2", ' \
     '"columnName": "credit_query_times_three_woe", ' \
     '"columnBinning": {"binCountWoe": [], "binBoundary": []}, ' \
     '"version": "", ' \
     '"columnStats": ' \
     '{"c": "阿萨德"}}'

print pmmlJson


