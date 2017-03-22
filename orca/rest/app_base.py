from flask import Flask, request
from flask_restful import Api
from util.restfultools import *


app = Flask(__name__)
api = Api(app)