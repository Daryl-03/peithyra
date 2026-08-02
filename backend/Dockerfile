# syntax=docker/dockerfile:1

# Build stage
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app

COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./

RUN sed -i 's/\r$//' gradlew \
    && chmod +x gradlew

# Resolve dependencies while build files are still isolated
RUN --mount=type=cache,target=/root/.gradle/caches \
    --mount=type=cache,target=/root/.gradle/wrapper \
    ./gradlew dependencies --no-daemon

COPY src ./src

RUN --mount=type=cache,target=/root/.gradle/caches \
    --mount=type=cache,target=/root/.gradle/wrapper \
    ./gradlew bootJar --no-daemon \
    && find build/libs \
       -maxdepth 1 \
       -type f \
       -name "*.jar" \
       ! -name "*-plain.jar" \
       -exec cp {} /app/application.jar \;


# Runtime stage
FROM eclipse-temurin:25-jre-alpine AS runner

WORKDIR /app

RUN addgroup -S spring \
    && adduser -S spring -G spring

COPY --from=builder \
     --chown=spring:spring \
     /app/application.jar \
     application.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]