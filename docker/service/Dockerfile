FROM steamtrade/skincoin-payment-service:v1.0

VOLUME "/logs"

ADD config/application.yml /config/application.yml

ENTRYPOINT ["./wait_for_it.sh", "mysql:3306", "--", "./wait_for_it.sh", "geth:8545", "--", "java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
