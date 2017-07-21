# coding=utf-8
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, DATETIME, CHAR, DECIMAL, BINARY, INT, Integer, String, ForeignKey, TEXT, \
    UniqueConstraint, Index
from sqlalchemy import create_engine
from sqlalchemy.sql import func
from sqlalchemy.orm.query import Query
from util import simple_util
import datetime
import json

Base = declarative_base()


def init_db():
    engine = create_engine("mysql+pymysql://root:Cisco123@127.0.0.1:3306/maas_tool_v3?charset=utf8", max_overflow=10,
                           echo=True)
    Base.metadata.create_all(engine)


def drop_db():
    engine = create_engine("mysql+pymysql://root:Cisco123@127.0.0.1:3306/maas_tool?charset=utf8", max_overflow=10,
                           echo=True)
    Base.metadata.drop_all(engine)

class AlchemyEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj,Query):
            return simple_util.query_to_base(obj)
        # 其他类型的数据按照默认的方式序列化成JSON
        return json.JSONEncoder.default(self, obj)

    # return json.JSONEncoder.default(self, obj)


class TableBase(AlchemyEncoder):
    id = Column(Integer, primary_key=True, autoincrement=True)
    is_deleted = Column(CHAR, nullable=False, server_default='0')
    create_date = Column(DATETIME, nullable=True, server_default=func.now())
    modify_date = Column(DATETIME, nullable=True, server_default=func.now())


# class User(Base):
#     __tablename__='maas_user'
#     id = Column(Integer, primary_key=True, autoincrement=True)
#     user_email = Column(String(50), nullable=True,)
#     user_password = Column(String(100), nullable=True)


class Model(TableBase, Base):
    __tablename__ = 'tool_model'
    model_name = Column(String(100), nullable=True)
    model_branch = Column(String(100), nullable=True)
    model_target = Column(String(100), nullable=True)
    remove_list = Column(String(5000), nullable=True)
    selected_list = Column(String(5000), nullable=True)
    file_path = Column(String(500), nullable=False)

    __table_args__ = (
        UniqueConstraint('model_name', 'model_branch', name='m_branch_name'),  # 联合唯一索引,name为索引的名字
        # Index('model_name','model_branch') #联合索引
    )


class ModelContent(TableBase, Base):
    __tablename__ = 'tool_model_content'
    model_name = Column(String(100), nullable=True)
    model_branch = Column(String(100), nullable=True)
    variable_name = Column(String(100), nullable=True)
    variable_iv = Column(String(100), nullable=True)
    binning_record = Column(TEXT, nullable=True)
    is_selected = Column(CHAR, nullable=False, server_default='0')

    __table_args__ = (
        UniqueConstraint('model_name', 'model_branch', name='c_branch_name'),
    )


class ModelSelectedVariable(TableBase, Base):
    __tablename__ = 'tool_model_selected_variable'
    model_name = Column(String(100), nullable=True)
    model_branch = Column(String(100), nullable=True)
    selected_variable = Column(String(15000), nullable=True)
    __table_args__ = (
        UniqueConstraint('model_name', 'model_branch', name='v_branch_name'),
    )


class User(TableBase, Base):
    """
    用户信息
    """
    __tablename__ = 'maas_user'
    user_nick = Column(String(50), nullable=True)  # 用户昵称
    user_email = Column(String(50), unique=True, nullable=True, index=True)  # 邮箱
    user_password = Column(String(100), nullable=True)  # 密码,使用md5加密


class Project(TableBase, Base):
    """
    工程信息
    """
    __tablename__ = 'maas_project'
    owner_id = Column(Integer, nullable=True)
    project_name = Column(String(50), nullable=True)
    project_task = Column(String(20), nullable=True)
    project_desc = Column(String(50), nullable=True)


class Source(TableBase, Base):
    """
    训练依赖的数据集
    """
    __tablename__ = 'maas_source'
    project_id = Column(Integer, nullable=True)  # 所属工程id
    set_name = Column(String(50), nullable=True)  # 用户指定的数据集名称
    file_name = Column(String(100), nullable=True)  # 已上传的文件的名称
    file_size = Column(String(10), nullable=True)  # 已上传的文件的大小
    file_path = Column(String(100), nullable=True)  # 已上传的文件的路径
    file_type = Column(CHAR, nullable=True)  # 文件类型 如excel,csv
    file_storage = Column(CHAR, nullable=False, server_default='0')  # 存储类型 0为local
    file_scope = Column(CHAR, nullable=True)  # 0 代表public 1代表private
    file_origin = Column(CHAR, nullable=True)  # 0 代表由用户上传
    file_readable = Column(CHAR, nullable=True)  # 0 代表是否可以被解析 1代表解析的时候出错
    available = Column(CHAR, nullable=False, server_default='0')  # 0 代表不可被使用 1代表可被使用


class Variable(TableBase, Base):
    """
    从数据集中获取的变量
    """
    __tablename__ = 'maas_variable'
    variable_name = Column(String(100), nullable=True)  # 变量名称
    source_id = Column(Integer, nullable=True)  # 所属数据集id
    usage = Column(CHAR, nullable=True)  # 0代表未被使用 1代表被使用 2代表作为target使用
    type = Column(String(12), nullable=True)
    total_count = Column(Integer, nullable=True)  # 总记录数
    non_missing_count = Column(Integer, nullable=True)  # 非空值记录数
    missing_count = Column(Integer, nullable=True)  # 空值记录数
    coverage = Column(String(30), nullable=True)  # 覆盖率
    unique_val = Column(Integer, nullable=True)
    min = Column(String(40), nullable=True)
    max = Column(String(40), nullable=True)
    mean = Column(String(40), nullable=True)


class Experiment(TableBase, Base):
    __tablename__ = 'maas_experiment'
    source_id = Column(Integer, nullable=True)
    project_id = Column(Integer, nullable=True)
    title = Column(String(40), nullable=True)
    algorithm = Column(CHAR, nullable=True)  # 算法类型 0代表LR(Logistic Regression)


init_db()
