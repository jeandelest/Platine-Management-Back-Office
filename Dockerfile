FROM eclipse-temurin:17.0.7_7-jre
WORKDIR /application
RUN rm -rf /application
ADD target/*.jar /application/app.jar
ENTRYPOINT ["java", "-jar",  "/application/app.jar"]