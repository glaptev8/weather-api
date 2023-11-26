FROM openjdk:17-jdk-slim-buster
WORKDIR /weatherapi

COPY build/libs/weatherapi-1.0-SNAPSHOT.jar /weatherapi/build/

WORKDIR /weatherapi/build

EXPOSE 8082

ENTRYPOINT java -jar weatherapi-1.0-SNAPSHOT.jar