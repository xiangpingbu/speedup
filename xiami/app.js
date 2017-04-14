var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var index = require('./routes/index');
var users = require('./routes/users');
var tool = require('./routes/tool');


var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
// app.use(express.static(path.join(__dirname, 'public')));
app.use('/public',express.static(path.join(__dirname, 'public')));
app.use('/static',express.static(path.join(__dirname, 'views')));
app.use('/', index);
app.use('/users', users);
app.use('/tool', tool);
// app.use('/tool', users);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  var env = req.app.get('env');
  res.locals.error = env === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

app.set('title', 'My Application');

// 只用于开发环境
if ('development' == app.get('env')) {
    app.set('py_uri', 'http://localhost:8091');
}

// 只用于生产环境
if ('production' == app.get('env')) {
    app.set('py_uri', 'http://sun.ecreditpal.com:8091');

}


module.exports = app;

// var server = app.listen(3000, function () {
//     var host = server.address().address;
//     var port = server.address().port;
//
//     console.log('Example app listening at http://%s:%s', host, port);
// });

