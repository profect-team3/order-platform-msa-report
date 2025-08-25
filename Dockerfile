FROM gradle:8.8-jdk17 AS builder
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle ./
COPY gradle ./gradle
COPY order-platform-msa-report ./order-platform-msa-report
COPY order-platform-msa-report/build.cloud.gradle ./order-platform-msa-report/build.gradle

RUN ./gradlew :order-platform-msa-report:bootJar -x test

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /workspace/order-platform-msa-report/build/libs/*.jar /app/application.jar

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app/application.jar"]
