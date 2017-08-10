FROM ethereum/client-go:release-1.6

RUN apk add --update expect && \
    mkdir /gethdata && \
    mkdir /etc/geth && echo "123" > /etc/geth/passwordfile && \
    mkdir /contract

COPY ./scripts/run.sh run.sh
COPY ./scripts/deploy.sh deploy.sh
COPY ./scripts/expect.sh /contract/expect.sh

RUN chmod +x run.sh && \
    chmod +x deploy.sh && \
    chmod +x /contract/expect.sh

VOLUME ["/gethdata"]

EXPOSE 8545
EXPOSE 30303
EXPOSE 30303/udp

ENTRYPOINT ["./run.sh"]
