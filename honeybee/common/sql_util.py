import pymysql


class util():
    def __init__(self):
        self.conn = pymysql.connect(host='127.0.0.1', port=3306, user='root', passwd='Cisco123', db='wool',charset="utf8")

    def query(self, sql, args=None):
        cur = self.conn.cursor
        cur =  cur(pymysql.cursors.DictCursor)
        # cur = self.conn.DictCursor()
        cur.execute(sql, args)
        return cur.fetchall()

    def execute(self, sql, args=None):
        try:
            cur = self.conn.cursor()
            result = cur.execute(sql, args)
            self.conn.commit()
            return result

        except Exception, e:
            print e
            self.conn.rollback()


util = util()
print util.query("select model_branch,remove_list from tool_model where  is_deleted = 0")
