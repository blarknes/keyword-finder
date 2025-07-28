# Build
FROM maven:3.8.8-eclipse-temurin-21-alpine AS build

COPY src /home/app/src
COPY pom.xml /home/app

RUN mvn -f /home/app/pom.xml clean package

# Run
FROM openjdk:21

COPY --from=build /home/app/target/keyword-finder-2.0.jar /bin/keywordfinder.jar

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/bin/keywordfinder.jar" ]
