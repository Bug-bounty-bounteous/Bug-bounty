FROM gradle:7.6.4-jdk17 AS build


WORKDIR /my-project
COPY --chown=gradle:gradle . .
RUN gradle build -x test


EXPOSE 8080

# FROM eclipse-temurin:17-jre-noble
# RUN mkdir /app
# WORKDIR /app

# COPY --from=build /my-project .
CMD ["./gradlew", "bootRun"]