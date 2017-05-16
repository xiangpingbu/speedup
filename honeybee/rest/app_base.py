from flask import Flask, request, send_from_directory
from flask_restful import Api
from util.restfultools import *


app = Flask(__name__)
api = Api(app)
# frontend pages
@app.route('/dash/<path:path>')
def send_file(path):
    return send_from_directory('../dashboard', path)