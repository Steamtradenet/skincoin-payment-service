## ############ ############ ############ ############ ########### ##
## DESCRIPTION:                                                    ##
##        Configuration parameters                                 ##
##                                                                 ##
## Copyright (C) 2017 Steamtrade Ltd - All Rights Reserved         ##
## Last revised 08/08/2017                                         ##
## ############ ############ ############ ############ ########### ##

# Mysql
MYSQL_ROOT_PASSWORD=root
MYSQL_USER=dbuser
MYSQL_PASSWORD=dbpass
MYSQL_DATABASE=payment_service
MYSQL_DATA_DIR=/mnt/skincoin/mysql

# Geth
GETH_DATA_DIR=/mnt/skincoin/geth
# Ethereum network. Possible values are: "live"(MAIN_NET), "private"(PRIVATE_NET) and "test"(TEST_NET)
ETHEREUM_NETWORK=live

# Skincoin Payment Service
SERVICE_PORT=8189
SERVICE_LOG_DIR=/mnt/skincoin/logs

# Admin UI
ADMIN_NAME=admin
ADMIN_PASSWORD=admin
JWT_SECRET_KEY=secretKey

# SkinCoin deployed address.
# Change it for "private" network only, i.e. in testing environment
SKINCOIN_ADDRESS=0x2bdc0d42996017fce214b21607a515da41a9e0c5
