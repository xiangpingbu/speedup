#!/bin/bash

. ../config

#rm -rf ./log
#python ./setup.py sdist
#scp -r ./dist/honeybee-0.0.tar.gz $SERVER_IP:$Maas_Home/
#-q 忽略任何对话和错误提示
#-T 不占用shell
ssh -tq $SERVER_IP << remotessh

#    tar -xzvf /homes/maas/honeybee-0.0.tar.gz -C /homes/maas/
    sh /homes/maas/maas/honeybee/python_proc.sh
remotessh

