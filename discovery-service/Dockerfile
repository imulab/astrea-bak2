FROM openjdk:8-jdk-alpine

COPY ./build/libs/discovery-service-*.jar discovery-service.jar

EXPOSE 8080
EXPOSE 35028

ENTRYPOINT ["java", "-jar", "/discovery-service.jar"]