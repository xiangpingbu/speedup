#!/bin/bash

. ./config


mvn clean package -Dmaven.test.skip=true
tmp_kill_proc_sh="_killprocess.sh"
scp -r ./kill_proc.sh $SERVER_IP:"~/$tmp_kill_proc_sh"

scp $RESOURCE_PATH $SERVER_IP:~/ROOT.war
scp -r $CONFIG_PATH $SERVER_IP:~/config-dir
ssh -Tq $SERVER_IP << remotessh
    ~/$tmp_kill_proc_sh
    rm -rf ~/$tmp_kill_proc_sh
    rm -rf ${Jetty_Home}/ROOT.war
    cp -r ~/config-dir ${Jetty_Home}/resources
    mv ~/ROOT.war ${Jetty_Home}/webapps
remotessh





