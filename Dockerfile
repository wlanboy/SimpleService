FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ADD bootstrap.properties bootstrap.properties
ADD application.yml application.yml
ADD logback.xml logback.xml
EXPOSE 8201
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
