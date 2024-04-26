FROM lliurex/sbuild:lliurex25
WORKDIR /app
COPY ./sbuild/bionic/lliurex_bionic /etc/schroot/chroot.d/
COPY ./sbuild/focal/lliurex_focal /etc/schroot/chroot.d/
COPY ./sbuild/lliurex22/lliurex_lliurex22 /etc/schroot/chroot.d/
COPY ./sbuild/jammy/lliurex_jammy /etc/schroot/chroot.d/
COPY ./sbuild/noble/lliurex_noble /etc/schroot/chroot.d/
COPY ./sbuild/04tmpfs /etc/schroot/setup.d/
COPY ./sbuild/bionic/sources.list /sbuild/bionic-amd64/etc/apt/
COPY ./sbuild/focal/sources.list /sbuild/focal-amd64/etc/apt/
COPY ./sbuild/lliurex22/sources.list /sbuild/lliurex22-amd64/etc/apt/
COPY ./sbuild/jammy/sources.list /sbuild/jammy-amd64/etc/apt/
COPY ./sbuild/noble/sources.list /sbuild/noble-amd64/etc/apt/
COPY ./sbuild/worker.py .
COPY ./sbuild/telegram_sender.py .
RUN chmod a+rx /etc/schroot/setup.d/04tmpfs
RUN apt update; apt install -y python3 openssh-server git python3-pip celery python3-celery python3-redis python3-requests
run pip3 install python-telegram-bot flower --break-system-packages
CMD /usr/bin/celery -A worker.celery worker -Q sbuild 
