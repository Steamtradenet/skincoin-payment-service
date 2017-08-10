#!/bin/bash

## ############ ############ ############ ############ ########### ##
## DESCRIPTION:                                                    ##
##        Script to generate configuration files for Mysql, Geth   ##
##        and SkinCoin payment service.                            ##
##                                                                 ##
## Copyright (C) 2017 Steamtrade Ltd - All Rights Reserved         ##
## Last revised 08/08/2017                                         ##
## ############ ############ ############ ############ ########### ##

# Import configuration parameters
. config.sh

# Declare variables
TARGET_DIR=build
TARGET_GETH_DIR=$TARGET_DIR/geth
TARGET_SERVICE_DIR=$TARGET_DIR/service

CONF_DIR=docker
CONF_GETH_DIR=$CONF_DIR/geth
CONF_SERVICE_DIR=$CONF_DIR/service

RESOURCE_DIR=src/main/resources

if [ -d "$BASE_DIR" ]; then
    echo "Remove build dir..."
    rm -rf "$BASE_DIR"
fi

echo "Prepare geth configs..."

mkdir -p "$TARGET_GETH_DIR"
cp -r $CONF_GETH_DIR/* $TARGET_GETH_DIR

echo "Prepare SkinCoin payment service configs..."
mkdir -p "$TARGET_SERVICE_DIR/config"

cp $CONF_SERVICE_DIR/application.tpl $TARGET_SERVICE_DIR/config/application.yml
cp $CONF_SERVICE_DIR/Dockerfile* $TARGET_SERVICE_DIR/Dockerfile


APPLICATION_FILE=$TARGET_SERVICE_DIR/config/application.yml
sed -i -e 's|\${ACTIVEMQ_USER}|'"$ACTIVEMQ_USER"'|g' $APPLICATION_FILE
sed -i -e 's|\${ACTIVEMQ_USER_PASSWORD}|'"$ACTIVEMQ_USER_PASSWORD"'|g' $APPLICATION_FILE
sed -i -e 's|\${MYSQL_ROOT_PASSWORD}|'"$MYSQL_ROOT_PASSWORD"'|g' $APPLICATION_FILE
sed -i -e 's|\${MYSQL_DATABASE}|'"$MYSQL_DATABASE"'|g' $APPLICATION_FILE
sed -i -e 's|\${MYSQL_USER}|'"$MYSQL_USER"'|g' $APPLICATION_FILE
sed -i -e 's|\${MYSQL_PASSWORD}|'"$MYSQL_PASSWORD"'|g' $APPLICATION_FILE
sed -i -e 's|\${SKINCOIN_ADDRESS}|'"$SKINCOIN_ADDRESS"'|g' $APPLICATION_FILE
sed -i -e 's|\${ADMIN_NAME}|'"$ADMIN_NAME"'|g' $APPLICATION_FILE
sed -i -e 's|\${ADMIN_PASSWORD}|'"$ADMIN_PASSWORD"'|g' $APPLICATION_FILE
sed -i -e 's|\${JWT_SECRET_KEY}|'"$JWT_SECRET_KEY"'|g' $APPLICATION_FILE

cp $APPLICATION_FILE $RESOURCE_DIR


echo "Prepare docker-compose.yml..."

cp $CONF_DIR/docker-compose.tpl docker-compose.yml

sed -i -e 's|\${GETH_DATA_DIR}|'"$GETH_DATA_DIR"'|g' docker-compose.yml
sed -i -e 's|\${ACTIVEMQ_DATA_DIR}|'"$ACTIVEMQ_DATA_DIR"'|g' docker-compose.yml
sed -i -e 's|\${MYSQL_ROOT_PASSWORD}|'"$MYSQL_ROOT_PASSWORD"'|g' docker-compose.yml
sed -i -e 's|\${MYSQL_DATABASE}|'"$MYSQL_DATABASE"'|g' docker-compose.yml
sed -i -e 's|\${MYSQL_USER}|'"$MYSQL_USER"'|g' docker-compose.yml
sed -i -e 's|\${MYSQL_PASSWORD}|'"$MYSQL_PASSWORD"'|g' docker-compose.yml
sed -i -e 's|\${MYSQL_DATA_DIR}|'"$MYSQL_DATA_DIR"'|g' docker-compose.yml
sed -i -e 's|\${SERVICE_PORT}|'"$SERVICE_PORT"'|g' docker-compose.yml
sed -i -e 's|\${SERVICE_LOG_DIR}|'"$SERVICE_LOG_DIR"'|g' docker-compose.yml
sed -i -e 's|\${ETHEREUM_NETWORK}|'"$ETHEREUM_NETWORK"'|g' docker-compose.yml

echo "OK"
