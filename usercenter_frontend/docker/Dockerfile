FROM nginx

WORKDIR /usr/share/nginx/html
USER root

COPY ./docker/nginx.conf /etc/nginx/nginx.conf
COPY ./dist /usr

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
