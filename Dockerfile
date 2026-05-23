FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/cv-scanner-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir temp-cvs
ENTRYPOINT ["java", "-jar", "app.jar"]
