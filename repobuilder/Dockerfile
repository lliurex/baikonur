FROM ubuntu:latest
WORKDIR /srv/app
COPY repobuilder/main_loop /usr/bin
RUN chmod +x /usr/bin/main_loop
RUN apt update;DEBIAN_FRONTEND=noninteractive apt install -y python3 openssh-server git python3-pip celery python3-celery python3-redis python3-requests reprepro sudo
RUN pip3 install flower #--break-system-packages
COPY repobuilder/distributions /usr/share/repobuilder/
COPY repobuilder/configurations /usr/share/baikonur/
COPY repobuilder/worker.py .
RUN chmod 770 /etc/sudoers
RUN echo '%reprepro ALL = (ALL) NOPASSWD: /usr/bin/reprepro' >> /etc/sudoers
RUN chmod 660 /etc/sudoers
RUN addgroup reprepro
CMD main_loop
