# Stage 1: Build the JAR
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the JAR
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/smart-uni-backend-0.0.1-SNAPSHOT.jar app.jar
ENV PORT=7860
EXPOSE 7860
ENTRYPOINT ["java", "-jar", "app.jar"]
