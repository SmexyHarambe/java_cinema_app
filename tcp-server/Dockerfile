FROM eclipse-temurin:21-jdk-alpine as build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 6002

ENTRYPOINT ["java", "-jar", "app.jar"]
