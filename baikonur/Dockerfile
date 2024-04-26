FROM ubuntu:latest
WORKDIR /app
COPY baikonur ./
RUN apt update && DEBIAN_FRONTEND=noninteractive apt install -y \
	python3-pyinotify \
	python3-debian \
	python3 \
	python3-requests \
	python3-pip \
	python3-celery \
	celery \
	python3-redis
CMD /app/baikonur
