#!/bin/bash

ORCA_HOME=/homes/maas/orca
PID_FILE=/homes/maas/orca/log/maas.pid
LOG_FILE=/homes/maas/orca/log/maas.log
ORCA_CONFIG=conf/gun.py

if [ -f $PID_FILE ];then
        pid=`cat $PID_FILE`
        echo $pid
        kill -9 $pid
   fi
    cd $ORCA_HOME
    echo `pwd`
    echo $ORCA_CONFIG
    nohup gunicorn -c $ORCA_CONFIG orca_server:app > $LOG_FILE 2>&1 &