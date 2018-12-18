FROM openjdk:8-jdk-alpine

COPY ./build/libs/authorize-service-*.jar authorize-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/authorize-service.jar"]