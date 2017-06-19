from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker


class Orm():
    engine = create_engine("mysql+pymysql://root:Cisco123@127.0.0.1:3306/maas_tool?charset=utf8", max_overflow=10,
                           echo=True)
    def __init__(self):
        DB_Session = sessionmaker(bind=self.engine)
        self.session = DB_Session()

    def __enter__(self):
        print 'open session'
        DB_Session = sessionmaker(bind=self.engine)
        self.session = DB_Session()
        return self

    def __exit__(self, type, value, trace):
        self.session.close()
        print 'close session'


def get_orm():
    return Orm()