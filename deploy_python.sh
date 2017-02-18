#!/bin/bash

. ./config

rm -rf ./orca/log
scp -r ./orca $SERVER_IP:$Maas_Home/
#-q 忽略任何对话和错误提示
#-T 不占用shell
ssh -tq $SERVER_IP << remotessh
    sh /homes/maas/orca/python_proc.sh
remotessh