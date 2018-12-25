FROM openjdk:8-jdk-alpine

COPY ./build/libs/token-endpoint-gateway-*.jar token-endpoint-gateway.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/token-endpoint-gateway.jar"]