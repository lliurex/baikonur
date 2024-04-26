FROM ubuntu:latest

#RUN apt update; apt install -y python3 devscripts python3-debian python3-flask gunicorn3
RUN apt update; apt install -y python3 python3-debian python3-flask gunicorn
RUN apt install -y tzdata
RUN apt install -y git
RUN apt install -y devscripts
WORKDIR /app
COPY packageFinder.py .
COPY packagesbaikonur.py .
COPY clone_github_repo /usr/bin/clone_github_repo
COPY templates ./templates

CMD gunicorn3 --bind 0.0.0.0:80 packagesbaikonur
