#!/bin/bash

## ############ ############ ############ ############ ########### ##
## DESCRIPTION:                                                    ##
##        Script to generate docker images for Mysql, Geth         ##
##        and SkinCoin payment service.                            ##
##                                                                 ##
## Copyright (C) 2017 Steamtrade Ltd - All Rights Reserved         ##
## Last revised 08/08/2017                                         ##
## ############ ############ ############ ############ ########### ##

echo "Build Geth image..."
cd build/geth
docker build -t skincoin/geth .

echo "Build SkinCoin payment service image..."
cd ../service
docker build -t skincoin/skincoin-payment-service:v1.0 .





