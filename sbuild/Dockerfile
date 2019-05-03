FROM lliurex/sbuild:bionic
COPY ./sbuild/getbuildfile /usr/bin
COPY ./sbuild/main_loop /usr/bin
COPY ./sbuild/stop_loop /usr/bin
COPY jenkins_pass /
COPY jenkins_user /
COPY ./sbuild/lliurex /etc/schroot/chroot.d/
COPY ./sbuild/sources.list /sbuild/bionic-amd64/etc/apt/
RUN chmod +x /usr/bin/main_loop
RUN chmod +x /usr/bin/stop_loop
RUN chmod +x /usr/bin/getbuildfile
RUN apt update; apt install -y python3 openssh-server openjdk-8-jdk
RUN useradd -G sbuild -ms /bin/bash -p $(cat /jenkins_pass) $(cat /jenkins_user) 
RUN rm jenkins_pass jenkins_user
CMD main_loop
