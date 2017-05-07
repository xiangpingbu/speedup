#!/bin/bash

. ../config

rm -rf ./log
python ./conf/setup.py sdist
scp -r ./dist/honeybee-0.0.tar.gz $SERVER_IP:$Maas_Home/
#-q 忽略任何对话和错误提示
#-T 不占用shell
ssh -tq $SERVER_IP << remotessh

    mv $PID_FILE /homes/maas/
    rm -rf /homes/maas/honeybee
    tar -xzvf /homes/maas/honeybee-0.0.tar.gz -C /homes/maas/
    mv /homes/maas/honeybee-0.0 /homes/maas/honeybee
    mkdir -p /homes/maas/honeybee/log
    sh /homes/maas/honeybee/conf/python_proc.sh
remotessh

    #sh /homes/maas/honeybee/python_proc.sh
