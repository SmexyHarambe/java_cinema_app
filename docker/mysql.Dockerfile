FROM mysql:8.0

ENV MYSQL_DATABASE=uasdisprog
ENV MYSQL_ROOT_PASSWORD=

COPY ../Disprog_UAS/DataUASDisprog.sql /docker-entrypoint-initdb.d/

EXPOSE 3306
