from hdfs.client import Client
import time
client = Client("http://10.10.10.103:50070")

with client.read('/user/lifeng/test/honeybee/hello') as fs:
    content = fs.read()
