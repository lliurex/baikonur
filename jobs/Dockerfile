FROM ubuntu:latest
WORKDIR /srv/app
RUN apt update;DEBIAN_FRONTEND=noninteractive apt install -y python3 python3-pip celery python3-celery python3-redis python3-requests
RUN pip3 install flower #--break-system-packages
COPY jobs/worker.py .
CMD celery -A worker.celery flower --port=80 --url_prefix=/jobs
