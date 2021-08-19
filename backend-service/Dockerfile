#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY backend-service-app /home/app/backend-service-app
COPY backend-service-app/pom.xml /home/app/backend-service-app
RUN mvn -f /home/app/backend-service-app/pom.xml clean package

#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /home/app/backend-service-app/target/*.jar /usr/local/lib/backend-service-app-app.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/backend-service-app-app.jar"]