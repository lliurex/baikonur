FROM lliurex/sbuild:bionic
COPY ./sbuild/getbuildfile /usr/bin
COPY ./sbuild/main_loop /usr/bin
COPY ./sbuild/stop_loop /usr/bin
COPY ./sbuild/checkpackageexists /usr/bin
COPY jenkins_pass /
COPY jenkins_user /
COPY ./sbuild/lliurex /etc/schroot/chroot.d/
COPY ./sbuild/sources.list /sbuild/bionic-amd64/etc/apt/
RUN mkdir -p /var/run/sshd
RUN chmod +x /usr/bin/main_loop
RUN chmod +x /usr/bin/stop_loop
RUN chmod +x /usr/bin/checkpackageexists
RUN chmod +x /usr/bin/getbuildfile
RUN apt update; apt install -y python3 openssh-server openjdk-8-jdk
RUN useradd -G sbuild -ms /bin/bash $(cat /jenkins_user)
RUN echo "$(cat /jenkins_user):$(cat /jenkins_pass)" | chpasswd
RUN rm jenkins_pass jenkins_user
CMD /usr/sbin/sshd -D
