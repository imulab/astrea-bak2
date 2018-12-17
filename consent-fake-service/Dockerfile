FROM openjdk:8-jdk-alpine

COPY ./build/libs/consent-fake-service-*.jar consent-fake-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/consent-fake-service.jar"]