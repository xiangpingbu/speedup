# coding=utf-8
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, DATETIME, CHAR, BINARY, INT, Integer, String, ForeignKey, UniqueConstraint, Index
from sqlalchemy.orm import sessionmaker, relationship
from sqlalchemy import create_engine
from sqlalchemy.sql import func

engine = create_engine("mysql+pymysql://root:Cisco123@127.0.0.1:3306/maas_tool?charset=utf8", max_overflow=10,
                       echo=True)
Base = declarative_base()


def init_db():
    Base.metadata.create_all(engine)


def drop_db():
    Base.metadata.drop_all(engine)


class Model(Base):
    __tablename__ = 'tool_model'
    id = Column(Integer, primary_key=True, autoincrement=True)
    model_name = Column(String(100), nullable=True)
    model_branch = Column(String(100), nullable=True)
    model_target = Column(String(100), nullable=True)
    remove_list = Column(String(5000), nullable=True)
    selected_list = Column(String(5000), nullable=True)
    create_date = Column(DATETIME, nullable=True, server_default=func.now())
    modify_date = Column(DATETIME, nullable=True, server_default=func.now())
    is_deleted = Column(CHAR, nullable=False, server_default='0')
    file_path = Column(String(500), nullable=False)

    __table_args__ = (
        UniqueConstraint('model_name', 'model_branch', name='m_branch_name'),  # 联合唯一索引,name为索引的名字
        # Index('model_name','model_branch') #联合索引
    )


class ModelContent(Base):
    __tablename__ = 'tool_model_content'
    id = Column(Integer, primary_key=True, autoincrement=True)
    model_name = Column(String(100), nullable=True)
    model_branch = Column(String(100), nullable=True)
    variable_name = Column(String(100), nullable=True)
    variable_iv = Column(String(100), nullable=True)
    binning_record = Column(String(200000), nullable=True)
    is_deleted = Column(CHAR, nullable=False, server_default='0')
    is_selected = Column(CHAR, nullable=False, server_default='0')
    create_date = Column(DATETIME, nullable=True, server_default=func.now())
    modify_date = Column(DATETIME, nullable=True, server_default=func.now())

    __table_args__ = (
        UniqueConstraint('model_name', 'model_branch', model_name='c_branch_name'),
    )


class ModelSelectedVariable(Base):
    __tablename__ = 'tool_model_selected_variable'
    id = Column(Integer, primary_key=True, autoincrement=True)
    model_name = Column(String(100), nullable=True)
    model_branch = Column(String(100), nullable=True)
    selected_variable = Column(String(15000), nullable=True)
    is_deleted = Column(CHAR, nullable=False, server_default='0')
    create_date = Column(DATETIME, nullable=True, server_default=func.now())
    modify_date = Column(DATETIME, nullable=True, server_default=func.now())

    __table_args__ = (
        UniqueConstraint('model_name', 'model_branch', model_name='v_branch_name'),
    )


init_db()

DB_Session = sessionmaker(bind=engine)
session = DB_Session()
