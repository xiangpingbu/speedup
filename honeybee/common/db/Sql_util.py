# -*- coding: UTF-8 -*-
from DB_connetion_pool import getPTConnection, PTConnectionPool
import pymysql


def query(sql,args= None):
    with getPTConnection() as db:
        try:
            cur = db.cursor
            cur.execute(sql,args)
            return db.cursor.fetchall()
        except Exception, e:
            print e
            db.conn.rollback()


def execute(sql, args=None):
    with getPTConnection() as db:
        try:
            cur = db.cursor
            result = cur.execute(sql, args)
            db.conn.commit()
            return result
        except Exception, e:
            print e
            db.conn.rollback()
