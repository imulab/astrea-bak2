FROM openjdk:8-jdk-alpine

COPY ./build/libs/authorize-endpoint-gateway-*.jar authorize-endpoint-gateway.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/authorize-endpoint-gateway.jar"]