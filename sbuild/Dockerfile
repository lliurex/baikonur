FROM lliurex/sbuild:lliurex23
COPY ./sbuild/getbuildfile /usr/bin
COPY ./sbuild/get_chroot_build /usr/bin
COPY ./sbuild/main_loop /usr/bin
COPY ./sbuild/stop_loop /usr/bin
COPY ./sbuild/checkpackageexists /usr/bin
COPY ./sbuild/telegram_sender /usr/bin
COPY ./sbuild/get_source_version /usr/bin
COPY jenkins_pass /
COPY jenkins_user /
COPY ./sbuild/bionic/lliurex_bionic /etc/schroot/chroot.d/
COPY ./sbuild/focal/lliurex_focal /etc/schroot/chroot.d/
COPY ./sbuild/lliurex22/lliurex_lliurex22 /etc/schroot/chroot.d/
COPY ./sbuild/jammy/lliurex_jammy /etc/schroot/chroot.d/
COPY ./sbuild/04tmpfs /etc/schroot/setup.d/
COPY ./sbuild/bionic/sources.list /sbuild/bionic-amd64/etc/apt/
COPY ./sbuild/focal/sources.list /sbuild/focal-amd64/etc/apt/
COPY ./sbuild/lliurex22/sources.list /sbuild/lliurex22-amd64/etc/apt/
COPY ./sbuild/jammy/sources.list /sbuild/jammy-amd64/etc/apt/
RUN mkdir -p /var/run/sshd
RUN chmod +x /usr/bin/main_loop
RUN chmod +x /usr/bin/stop_loop
RUN chmod +x /usr/bin/checkpackageexists
RUN chmod +x /usr/bin/getbuildfile
RUN chmod +x /usr/bin/get_chroot_build
RUN /usr/bin/telegram_sender
RUN /usr/bin/get_source_version
RUN chmod a+rx /etc/schroot/setup.d/04tmpfs
RUN apt update; apt install -y python3 openssh-server openjdk-8-jdk git python3-pip
run pip3 install python-telegram-bot
RUN useradd -G sbuild -ms /bin/bash $(cat /jenkins_user)
RUN echo "$(cat /jenkins_user):$(cat /jenkins_pass)" | chpasswd
RUN rm jenkins_pass jenkins_user
CMD /usr/sbin/sshd -D
