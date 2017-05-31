from flask import Flask, request, send_from_directory
from flask_restful import Api
from util.restful_tools import *
from flask_environments import Environments
from common import global_value



app = Flask(__name__)
import  os
# os.environ['FLASK_ENV'] = 'PRODUCTION'
env = Environments(app)
env.from_object('conf.config')
api = Api(app)
