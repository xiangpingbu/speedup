from rest.app_base import *


@app.route("/test", methods=['GET'])
def test_error_handler():
    a = Exception()
    a.message = 'hello'
    raise a
