FROM openjdk:8-jdk-alpine

COPY ./build/libs/authorize-code-flow-service-*.jar authorize-code-flow-service.jar

EXPOSE 35127
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/authorize-code-flow-service.jar"]