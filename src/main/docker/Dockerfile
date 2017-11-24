FROM anapsix/alpine-java:8

ADD skincoin-payment-service-1.2.jar app.jar
ADD wait_for_it.sh wait_for_it.sh

RUN bash -c 'touch /app.jar' && \
    chmod +x wait_for_it.sh && \
    mkdir /config && \
    mkdir /logs

VOLUME "/logs"

ADD config/application.yml /config/application.yml

ENTRYPOINT ["./wait_for_it.sh", "mysql:3306", "--", "./wait_for_it.sh", "geth:8545", "--", "java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
