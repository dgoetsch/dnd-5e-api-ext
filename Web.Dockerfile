FROM openjdk:14-alpine AS build

RUN apk update && apk add --no-cache unzip
RUN mkdir -p /home/web
ADD . /home/web/src/
WORKDIR /home/web/src

RUN ./gradlew --no-daemon :web:build
WORKDIR /home/web
RUN mkdir app
RUN unzip -d /home /home/web/src/web/build/distributions/web.zip
RUN ls app

FROM openjdk:14-alpine
COPY --from=build /home/web /home/web
ENTRYPOINT "/home/web/bin/web"
