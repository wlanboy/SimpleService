FROM eclipse-temurin:21-jre-noble
VOLUME /tmp

WORKDIR /
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ADD application.yml application.yml
EXPOSE 8201
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
