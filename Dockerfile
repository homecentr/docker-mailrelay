FROM alpine:3.16

ARG MAILRELAY_VERSION="v1.04"

ADD https://github.com/wiggin77/mailrelay/releases/download/${MAILRELAY_VERSION}/mailrelay-linux-amd64 /usr/local/bin/mailrelay

RUN addgroup --gid 1000 nonroot && \
    adduser -u 1000 -G nonroot -D -H -g "" nonroot && \
    chmod a+x /usr/local/bin/mailrelay && \
    apk add --no-cache \
        ca-certificates=20211220-r0 \
        libc6-compat=1.2.3-r0

USER 1000:1000

VOLUME [ "/config" ]

ENTRYPOINT [ "/usr/local/bin/mailrelay" ]
CMD [ "--config=/config/mailrelay.json" ]