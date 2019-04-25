FROM ubuntu_update
RUN apt install -y openssh-server rssh
RUN mkdir /var/run/sshd
RUN sed -i -e "s/#allowscp/allowscp/g" /etc/rssh.conf
COPY sshd_config /etc/ssh/sshd_config
CMD /usr/sbin/sshd -D
