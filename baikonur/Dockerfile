FROM ubuntu:latest
WORKDIR /app
COPY baikonur ./
RUN apt update && apt install -y \
	python3-pyinotify \
	python3-debian \
	python3 \
	python3-requests \
	python3-pip \
	&& pip3 install python-jenkins
CMD /app/baikonur
