FROM ubuntu:18.04
RUN apt update; apt install -y sudo reprepro openssh-server openjdk-8-jdk 
COPY repobuilder/distributions /usr/share/repobuilder/
COPY jenkins_pass /
COPY jenkins_user /
COPY repobuilder/configurations /usr/share/baikonur/
COPY repobuilder/main_loop /usr/bin
RUN chmod 770 /etc/sudoers
RUN echo '%reprepro ALL = (ALL) NOPASSWD: /usr/bin/reprepro' >> /etc/sudoers
RUN chmod 660 /etc/sudoers
RUN chmod +x /usr/bin/main_loop
RUN mkdir -p /var/run/sshd
RUN addgroup reprepro
RUN useradd -G www-data,reprepro -ms /bin/bash $(cat /jenkins_user)
RUN echo "$(cat /jenkins_user):$(cat /jenkins_pass)" | chpasswd
RUN rm /jenkins_pass /jenkins_user
WORKDIR /srv/repository 
CMD /usr/bin/main_loop