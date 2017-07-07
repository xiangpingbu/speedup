# coding=utf-8
from sqlalchemy import create_engine

# 创建引擎
# max_overflowwe
engine = create_engine("mysql+pymysql://root:Cisco123@127.0.0.1:3306/maas_tool", max_overflow=10)
# 执行sql语句
# engine.execute("INSERT INTO user (name) VALUES ('dadadadad')")

result = engine.execute('select * from tool_model')
res = result.fetchall()
print(res)


