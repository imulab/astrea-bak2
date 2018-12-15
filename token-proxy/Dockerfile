FROM openjdk:8-jdk-alpine

COPY ./build/libs/token-proxy-*.jar token-proxy.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/token-proxy.jar"]