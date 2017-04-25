#!/usr/bin/python
import MySQLdb

def convert_n_bytes(n, b):
    bits = b*8
    return (n + 2**(bits-1)) % 2**bits - 2**(bits-1)

def convert_4_bytes(n):
    return convert_n_bytes(n, 4)

def getHashCode(s):
    h = 0
    n = len(s)
    for i, c in enumerate(s):
        h = h + ord(c)*31**(n-1-i)
    return convert_4_bytes(h)


conn = MySQLdb.connect(host="localhost",    # your host, usually localhost
                       user="root",         # your username
                       passwd="Cisco123",  # your password
                       db="ccnb_model_data")        # name of the data base

# you must create a Cursor object. It will let
#  you execute all the queries you need
cur = conn.cursor()
i = 0
with open('/home/lifeng/Pruned_new.tsv') as fp:
    for line in fp:
        data = line.split('\t')
        key = data[0]
        code = getHashCode(key)
        table = "ccnb_v2_"+ str(abs(code)% 10)
        cur.execute("INSERT INTO "+ table + " (model_key, model_value) VALUES (%s,%s)",(data[0],data[1]))
        i += 1
        if (i % 1000 == 0):
            conn.commit()
            print(str(i) + " lines done")
conn.close()


