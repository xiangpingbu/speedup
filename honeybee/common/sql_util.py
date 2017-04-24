import pymysql


class util():
    def __init__(self):
        self.conn = pymysql.connect(host='127.0.0.1', port=3306, user='root', passwd='Cisco123', db='wool',charset="utf8")

    def query(self, sql, args=None):
        cur = self.conn.cursor()
        cur.execute(sql, args)
        return cur.fetchall()

    def execute(self, sql, args=None):
        try:
            cur = self.conn.cursor()
            result = cur.execute(sql, args)
            self.conn.commit()
            return result

        except:
            self.conn.rollback()


util = util()
