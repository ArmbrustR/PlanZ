FROM openjdk:15

MAINTAINER REM Java 21 1 <rafael.armbrust@gmail.com>

ADD backend/target/github-bingo-master.jar app.jar

CMD ["sh", "-c", "java -Dserver.port=$PORT -Dspring.data.mongodb.uri=$MONGODB_URI -jar /app.jar"]
