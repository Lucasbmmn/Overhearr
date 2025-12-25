# --- Stage 1: Build the Application ---
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app

COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

COPY src/ src/

RUN ./gradlew bootJar --no-daemon -x test

# --- Stage 2: Run the Application ---
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

RUN addgroup -S overhearr && adduser -S overhearr -G overhearr
USER overhearr:overhearr

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]