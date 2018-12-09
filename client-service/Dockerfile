FROM openjdk:8-jdk-alpine

COPY ./build/libs/client-service-*.jar client-service.jar

EXPOSE 8080
EXPOSE 35027

ENTRYPOINT ["java", "-jar", "/client-service.jar"]