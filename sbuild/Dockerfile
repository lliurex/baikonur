FROM ubuntu:18.04
COPY main_loop /usr/bin
COPY stop_loop /usr/bin
RUN chmod +x /usr/bin/main_loop
RUN chmod +x /usr/bin/stop_loop
RUN apt update; apt install -y python3 sbuild openssh-server openjdk-8-jdk
RUN useradd -ms /bin/bash -p $(cat /jenkins_pass) $(cat /jenkins_user)
CMD main_loop