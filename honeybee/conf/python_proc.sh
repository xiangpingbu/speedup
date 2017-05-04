#!/bin/bash

ORCA_HOME=/homes/maas/honeybee
PID_FILE=/homes/maas/maas.pid
LOG_FILE=/homes/maas/honeybee/log/maas.log
ORCA_CONFIG=conf/gun.py

if [ -f $PID_FILE ];then
        pid=`cat $PID_FILE`
        echo $pid
        kill -9 $pid
fi

    cd $ORCA_HOME/conf
    echo `pwd`
    echo $ORCA_CONFIG
    nohup gunicorn -c gun.py honeybee_server:app > $LOG_FILE 2>&1 &