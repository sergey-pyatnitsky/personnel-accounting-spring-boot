FROM openjdk:8-jdk
ARG JAR_FILE=build/libs/personnel-accounting-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]