FROM bitnami/minideb@sha256:d32c928d4433618ac1aad9e193122070d8e93c1a910b3885d98d090ce4320f2e

ARG MAILRELAY_VERSION="v1.04"

ADD https://github.com/wiggin77/mailrelay/releases/download/${MAILRELAY_VERSION}/mailrelay-linux-amd64 /usr/local/bin/mailrelay

RUN addgroup --gid 1000 nonroot && \
    adduser --uid 1000 --gid 1000 --disabled-password --no-create-home --gecos "" nonroot && \
    chmod a+x /usr/local/bin/mailrelay

USER 1000:1000

VOLUME [ "/config" ]

ENTRYPOINT [ "/usr/local/bin/mailrelay" ]
CMD [ "--config=/config/mailrelay.json" ]