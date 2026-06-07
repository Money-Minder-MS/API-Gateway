FROM eclipse-temurin:17
COPY target/api-gateway-0.0.1-SNAPSHOT.jar api-gateway.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]