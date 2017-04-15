#!/bin/bash

. ./config

rm -rf ./honeybee/log
scp -r ./honeybee $SERVER_IP:$Maas_Home/
#-q 忽略任何对话和错误提示
#-T 不占用shell
ssh -tq $SERVER_IP << remotessh
    sh /homes/maas/honeybee/python_proc.sh
remotessh