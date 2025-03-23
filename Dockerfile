FROM eclipse-temurin:17-jdk-alpine
COPY ./build/libs/*SNAPSHOT.jar project.jar
COPY ./src/main/resources/app/application.yml /app/application.yml
ENTRYPOINT ["java", "-jar", "project.jar"]
