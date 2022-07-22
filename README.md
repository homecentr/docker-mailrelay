[![Project status](https://badgen.net/badge/project%20status/stable%20%26%20actively%20maintaned?color=green)](https://github.com/homecentr/docker-mailrelay/graphs/commit-activity) [![](https://badgen.net/github/label-issues/homecentr/docker-mailrelay/bug?label=open%20bugs&color=green)](https://github.com/homecentr/docker-mailrelay/labels/bug) [![](https://badgen.net/github/release/homecentr/docker-mailrelay)](https://hub.docker.com/repository/docker/homecentr/mailrelay)
[![](https://badgen.net/docker/pulls/homecentr/mailrelay)](https://hub.docker.com/repository/docker/homecentr/mailrelay) 
[![](https://badgen.net/docker/size/homecentr/mailrelay)](https://hub.docker.com/repository/docker/homecentr/mailrelay)

![CI/CD on master](https://github.com/homecentr/docker-mailrelay/workflows/CI/CD%20on%20master/badge.svg)


# Homecentr - mailrelay

## Usage

```yml
version: "3.7"
services:
  mailrelay:
    build: .
    image: homecentr/mailrelay
```

## Exposed ports

| Port | Protocol | Description |
|------|------|-------------|
| ? (2525 is recommended, defined in the config file) | TCP | SMTP |

## Volumes

| Container path | Description |
|------------|---------------|
| /config | The container expects config file at `/config/mailrelay.json` |

## Security
The container is regularly scanned for vulnerabilities and updated. Further info can be found in the [Security tab](https://github.com/homecentr/docker-mailrelay/security).

### Container user
The container uses UID:GID of 1000:1000 by default. The UID:GID can be changed as needed.