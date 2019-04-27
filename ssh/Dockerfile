FROM ubuntu:18.04
RUN apt update; apt install -y openssh-server rssh
RUN addgroup lliurex
RUN mkdir -p /var/run/sshd
RUN mkdir /inbox
RUN chown root:lliurex /inbox
RUN chmod g+w /inbox
RUN sed -i -e "s/#allowscp/allowscp/g" /etc/rssh.conf
COPY sshd_config /etc/ssh/sshd_config
CMD /usr/sbin/sshd -D
