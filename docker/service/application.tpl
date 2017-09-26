########################################################################
# DEFAULT PROFILE
########################################################################
server.port: 8180

admin:
  username: ${ADMIN_NAME}
  password: ${ADMIN_PASSWORD}
  jwt.secretKey: ${JWT_SECRET_KEY}

ethereumService:
  skincoin.deployed-address: '${SKINCOIN_ADDRESS}'
  accountCreator.pool.size: 3
  confirmationCount: 30
  gasLimit: 90000
  gasPriceLimit: 46000000000
  watch:
    notification.poll.period: 5000
    accountCreator.poll.period: 10000
    newTransaction.poll.period: 5000
    pendingTransaction.poll.period: 5000
    newTransactionAnalyser.poll.period: 5000
    internalPayouts.poll.period: 5000
web3j:
  client-address: http://localhost:8545

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/${MYSQL_DATABASE}?useUnicode=true&useFastDateParsing=false&characterEncoding=UTF-8
    username: ${MYSQL_USER}
    password: ${MYSQL_PASSWORD}
    driver-class-name: com.mysql.jdbc.Driver
# Keep connection active
    testWhileIdle: true
    timeBetweenEvictionRunsMillis: 60000
    validationQuery: SELECT 1
  jpa:
    show-sql: false
    hibernate.naming-strategy: org.hibernate.cfg.ImprovedNamingStrategy
    properties:
      hibernate.dialect: org.hibernate.dialect.MySQL5Dialect
      hibernate.cache.use_second_level_cache: false
      hibernate.cache.use_query_cache: false
# FLyway DB
flyway:
  locations: classpath:/database/mysql
---
########################################################################
# DOCKER PROFILE
########################################################################
spring:
  profiles: docker
  datasource:
    url: jdbc:mysql://mysql:3306/${MYSQL_DATABASE}?useUnicode=true&useFastDateParsing=false&characterEncoding=UTF-8
    username: ${MYSQL_USER}
    password: ${MYSQL_PASSWORD}
web3j:
  client-address: http://geth:8545
