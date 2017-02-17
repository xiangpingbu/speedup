#!/bin/bash

. ./config

scp -r ./orca $SERVER_IP:$Maas_Home/
