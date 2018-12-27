FROM openjdk:8-jdk-alpine

COPY ./build/libs/client-credentials-flow-service-*.jar client-credentials-flow-service.jar

EXPOSE 35327

ENTRYPOINT ["java", "-jar", "/client-credentials-flow-service.jar"]