########################################################################
# DEFAULT PROFILE
########################################################################
server.port: 8180

admin:
  username: ${ADMIN_NAME}
  password: ${ADMIN_PASSWORD}
  jwt.secretKey: ${JWT_SECRET_KEY}

ethereumService:
  enabledDebug: true
  accountCreator.pool.size: 1
  watch:
    notification.poll.period: 5000
    accountCreator.poll.period: 10000
    newTransaction.poll.period: 5000
    pendingTransaction.poll.period: 5000

web3j:
  client-address: http://localhost:8545
skincoin:
  deployed-address: '${SKINCOIN_ADDRESS}'

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/${MYSQL_DATABASE}?useUnicode=true&useFastDateParsing=false&characterEncoding=UTF-8
    username: ${MYSQL_USER}
    password: ${MYSQL_PASSWORD}
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
