# Build stage
FROM amazoncorretto:17-alpine-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean build -x test

# Run stage
FROM amazoncorretto:17-alpine-jdk

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 19091

ARG PROFILE_ACTIVE
ENV SPRING_PROFILES_ACTIVE=${PROFILE_ACTIVE}

# Service URL environment variables
ENV FILE_SERVICE_URL=${FILE_SERVICE_URL}
ENV POST_SERVICE_URL=${POST_SERVICE_URL}
ENV ALARM_SERVICE_URL=${ALARM_SERVICE_URL}
ENV CHAT_SERVICE_URL=${CHAT_SERVICE_URL}

ENTRYPOINT ["java", "-Djasypt.encryptor.password=${JASYPT_PASSWORD}", "-Djava.net.preferIPv4Stack=true", "-jar", "/app.jar"]