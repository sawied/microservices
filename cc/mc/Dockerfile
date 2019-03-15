FROM python:3

MAINTAINER MVTM sawied danan.2009@gmail.com

WORKDIR /usr/src/app


COPY requirements.txt ./

RUN pip install --no-cache-dir -r requirements.txt


COPY mongo-connector-conf.json ./

ENTRYPOINT ["mongo-connector", "-c", "mongo-connector-conf.json"]


