FROM openjdk:8-jdk-alpine

COPY ./build/libs/authorize-proxy-*.jar authorization-proxy.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/authorization-proxy.jar"]