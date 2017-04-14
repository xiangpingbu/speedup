#!/bin/bash

. ./config


sh mvn clean package -Dmaven.test.skip=true
tmp_kill_proc_sh="_killprocess.sh"
scp ./kill_proc.sh $SERVER_IP:"~/$tmp_kill_proc_sh"

#mvn clean package
scp $RESOURCE_PATH $SERVER_IP:~/ROOT.war
ssh -Tq $SERVER_IP << remotessh
    ~/$tmp_kill_proc_sh
    rm -rf ~/$tmp_kill_proc_sh
    rm -rf ${Jetty_Home}/ROOT.war
    mv ~/ROOT.war ${Jetty_Home}/webapps
remotessh





