FROM maven:3.8.3-openjdk-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

CMD ["java","-jar","/app/target/usercenter_backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
