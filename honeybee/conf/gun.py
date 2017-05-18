# coding=utf-8
import os
import gevent.monkey
gevent.monkey.patch_all()

import multiprocessing

debug = True
loglevel = 'info'
bind = '0.0.0.0:8091'
pidfile = 'log/maas.pid'
logfile = 'log/maas.log'

#启动的进程数
# workers = multiprocessing.cpu_count() * 2 + 1
workers = 1
worker_class = 'gunicorn.workers.ggevent.GeventWorker'

x_forwarded_for_header = 'X-FORWARDED-FOR'