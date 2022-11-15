FROM postgres:latest

COPY ./ddl/* /docker-entrypoint-initdb.d
COPY ./dml/* /docker-entrypoint-initdb.d