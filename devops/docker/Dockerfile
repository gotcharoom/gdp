FROM openjdk:17-jdk-slim
WORKDIR /app
ARG JAR_FILE=app.jar
COPY ${JAR_FILE} app.jar
COPY entrypint.sh entrypoint.sh
ENTRYPOINT ["sh", "entrypoint.sh"]