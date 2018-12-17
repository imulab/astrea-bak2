FROM openjdk:8-jdk-alpine

COPY ./build/libs/login-fake-service-*.jar login-fake-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/login-fake-service.jar"]