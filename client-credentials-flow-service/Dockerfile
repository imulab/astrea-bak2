FROM openjdk:8-jdk-alpine

COPY ./build/libs/client-credentials-flow-service-*.jar client-credentials-flow-service.jar

EXPOSE 35327
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/client-credentials-flow-service.jar"]