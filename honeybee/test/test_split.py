import re

@outputSchema("element:tuple(id:long,customer cid:chararray, total_cnt:long)")
def process(bag):
    return findSubstring

def findSubstring(string, substring, times):
    start = 0
    current = 0
    a = []
    for i in range(1, times + 1):
        current = string.find(substring, current + 1)
        a.append(string[start:current])
        start = current + 1
    a.append(string[start:len(string)])
    return a



# a = findSubstring(string, ',', 3)
# print a
