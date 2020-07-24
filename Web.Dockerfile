FROM adoptopenjdk/openjdk14:x86_64-ubuntu-jdk-14.0.1_7 AS build

RUN mkdir -p /home/web
ADD . /home/web/src/
WORKDIR /home/web/src

RUN ./gradlew --no-daemon :web:app:build
# WORKDIR /home/web
# RUN mkdir web
# RUN ls web

FROM nginx:alpine

COPY --from=build /home/web/src/web/app/build/distributions/* /usr/share/nginx/html/
RUN ls /usr/share/nginx/html

# FROM adoptopenjdk/openjdk14:x86_64-ubuntu-jdk-14.0.1_7
# RUN mkdir /app
# COPY --from=build /home/web/src/web/app/build/libs/app.jar /app/app.jar
# WORKDIR /app

# CMD ["java", "-server", "-jar", "app.jar"]

