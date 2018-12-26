FROM openjdk:8-jdk-alpine

COPY ./build/libs/implicit-flow-service-*.jar implicit-flow-service.jar

EXPOSE 35227

ENTRYPOINT ["java", "-jar", "/implicit-flow-service.jar"]