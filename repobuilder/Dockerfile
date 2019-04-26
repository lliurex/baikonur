FROM ubuntu:18.04
RUN apt update; apt install -y reprepro openssh-server 
COPY distributions /usr/share/repobuilder/
COPY ../jenkins_pass /
COPY ../jenkins_user /
RUN useradd -ms /bin/bash -p $(cat /jenkins_pass) $(cat /jenkins_user)
WORKDIR /srv/repository 
CMD sleep infinity