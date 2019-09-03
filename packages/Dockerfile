FROM ubuntu:18.04

RUN apt update; apt install python3 devscripts python3-debian python3-flask gunicorn3
WORKDIR /app
COPY packageFinder.py .
COPY packagesbaikonur.py .
COPY templates .

CMD gunicorn3 --bind 0.0.0.0:80 packagesbaikonur
