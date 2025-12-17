FROM eclipse-temurin:17-jre
COPY target/webhook-service-1.0.0.jar app.jar
EXPOSE 8005
ENTRYPOINT ["java", "-jar", "/app.jar"]