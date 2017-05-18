#!/bin/bash


HOME=/homes/maas/maas/honeybee
#HOME=/Users/lifeng/Work/code/maas/honeybee
PID_FILE=$HOME/log/maas.pid
LOG_FILE=$HOME/log/maas.log

cd $HOME
echo "current path `pwd`"
git pull;

if [ -f $PID_FILE ];then
        pid=`cat $PID_FILE`
        echo "will kill the pid: $pid"
        kill -9 $pid
fi

nohup gunicorn -c conf/gun.py honeybee_server:app > $LOG_FILE 2>&1 &