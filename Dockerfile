FROM openjdk:15

MAINTAINER Rafael Armbrust <rafael.armbrust@gmail.com>

ADD backend/target/github-bingo-master.jar app.jar

CMD ["sh", "-c", "java -Dserver.port=$PORT -Dspring.data.mongodb.uri=$MONGODB_URI -jar /app.jar"]
