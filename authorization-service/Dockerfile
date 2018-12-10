FROM openjdk:8-jdk-alpine

COPY ./build/libs/authorization-service-*.jar authorization-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/authorization-service.jar"]