FROM openjdk:14-alpine AS build

RUN apk update && apk add --no-cache nodejs npm
RUN mkdir -p /home/web
ADD . /home/web/src/
WORKDIR /home/web/src

RUN ./gradlew --no-daemon --debug :web:app:build
WORKDIR /home/web
RUN mkdir web
RUN ls web

FROM nginx:alpine
RUN mkdir /home/static
COPY --from=build /home/web/src/web/app/build/distributions /usr/share/nginx/html
RUN ls /usr/share/nginx/html
