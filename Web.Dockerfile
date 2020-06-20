FROM openjdk:14-alpine AS build

RUN adduser -D --home /home/web --uid 1001 web web
RUN apk update && apk add --no-cache unzip
USER web

ADD --chown=web . /home/web/src/
WORKDIR /home/web/src

RUN ./gradlew --no-daemon :web:build
WORKDIR /home/web
RUN mkdir app
RUN unzip -d /home /home/web/src/web/build/distributions/web.zip
RUN ls app

FROM openjdk:14-alpine
RUN adduser -D --home /home/web --uid 1001 web web

COPY --from=build /home/web /home/web
ENTRYPOINT "/home/web/bin/web"
