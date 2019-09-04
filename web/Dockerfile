FROM nginx:latest
COPY web/default.conf /etc/nginx/conf.d/
COPY isomaker_path /
COPY isos_path /
RUN sed -i -e "s%@_@ISOMAKER@_@%"$(cat /isomaker_path)"%g" /etc/nginx/conf.d/default.conf
RUN sed -i -e "s%@_@ISOS@_@%"$(cat /isos_path)"%g" /etc/nginx/conf.d/default.conf