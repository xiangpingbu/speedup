#!/bin/bash

Jetty_Home=/homes/maas/jetty-distribution-9.4.1.v20170120
function kill_by_pid(){
  echo 'ready to get pid of jetty server'
pid=`ps -ef | grep $Jetty_Home | grep -v grep | awk '{print $2}'`
if [ ! $pid ] && [ ! -f $Jetty_Home/jetty.state ]; then
        echo "no server is running"
        sh ${Jetty_Home}/bin/jetty.sh start
    else
#            echo 'pid:'$pid
#            kill -9 $pid
#            echo "server has stopped"
#            echo "will restart server"
            sh ${Jetty_Home}/bin/jetty.sh stop
            sh ${Jetty_Home}/bin/jetty.sh start
fi
}

kill_by_pid