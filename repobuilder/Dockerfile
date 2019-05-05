FROM ubuntu:18.04
RUN apt update; apt install -y reprepro openssh-server openjdk-8-jdk 
COPY repobuilder/distributions /usr/share/repobuilder/
COPY jenkins_pass /
COPY jenkins_user /
RUN mkdir -p /var/run/sshd
RUN useradd -ms /bin/bash $(cat /jenkins_user)
RUN echo "$(cat /jenkins_user):$(cat /jenkins_pass)" | chpasswd
RUN rm /jenkins_pass /jenkins_user
WORKDIR /srv/repository 
CMD /usr/sbin/sshd -D