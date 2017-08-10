version: '2'
services:
  geth:
    image: skincoin/geth
    restart: always
    ports:
     - "127.0.0.1:8545:8545"
    volumes:
     - ${GETH_DATA_DIR}:/gethdata
    environment:
     - NETWORK=${ETHEREUM_NETWORK}
  mysql:
    image: mysql:5.7
    restart: always
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
      - MYSQL_DATABASE=${MYSQL_DATABASE}
      - MYSQL_USER=${MYSQL_USER}
      - MYSQL_PASSWORD=${MYSQL_PASSWORD}
    volumes:
      - ${MYSQL_DATA_DIR}:/var/lib/mysql
  skincoin-payment-service:
    image: skincoin/skincoin-payment-service:v1.0
    restart: always
    links:
      - geth
      - mysql
    volumes:
      - ${SERVICE_LOG_DIR}:/logs
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    ports:
      - "${SERVICE_PORT}:8180"
