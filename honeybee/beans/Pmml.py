# coding=utf-8
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
        self.columnBinning = None