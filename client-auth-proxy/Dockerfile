FROM openjdk:8-jdk-alpine

COPY ./build/libs/client-auth-proxy-*.jar client-auth-proxy.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/client-auth-proxy.jar"]