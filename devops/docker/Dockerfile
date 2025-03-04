FROM openjdk:21-jdk-slim
WORKDIR /app
ARG JAR_FILE=app.jar
COPY ${JAR_FILE} app.jar
COPY entrypoint.sh entrypoint.sh
ENTRYPOINT ["sh", "entrypoint.sh"]