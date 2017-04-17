var express = require('express');
var router = express.Router();
var request = require('request');
var app = express();

router.all("/:key", function(req, res) {

    var method = req.method.toUpperCase();
    console.log(method);
    var key = req.params.key;
    var url = req.app.get("py_uri")+"/es/resource/"+key;
    console.log(url);

    var options = {
        headers: {"Connection": "close"},
        url: url,
        method: method,
        json: true,
        body: req.body
    };


    request(options, callback);

    function callback(error, response, data) {
        if (!error && response.statusCode === 200) {
            // console.log('------接口数据------',JSON.stringify(data));
            return res.json(data)
        }
    }
});

module.exports = router;