FROM ubuntu:18.04
WORKDIR /app
COPY baikonur ./
RUN apt update; apt install -y python3-pyinotify python3-debian python3 python3-requests
CMD /app/baikonur