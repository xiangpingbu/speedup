var express = require('express');
var router = express.Router();
var request = require('request');

router.all("/init", function(req, res) {

    var method = req.method.toUpperCase();

    var url = 'http://localhost:8091/tool/init';

    var options = {
        headers: {"Connection": "close"},
        url: url,
        method: method,
        json: true,
        body: req.body
    };


    request(options, callback);
});

module.exports = router;


function callback(error, response, data) {
    if (!error && response.statusCode == 200) {
        console.log('------接口数据------',JSON.stringify(data));
        return res.json(data)
    }
}