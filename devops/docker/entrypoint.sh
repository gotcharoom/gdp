#!/bin/sh
echo "Starting application..."
exec java -jar -Dspring.profiles.active=deploy /app/app.jar
