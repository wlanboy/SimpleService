![Java CI with Maven](https://github.com/wlanboy/SimpleService/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master) ![Docker build and publish image](https://github.com/wlanboy/SimpleService/workflows/Docker%20build%20and%20publish%20image/badge.svg)

# SimpleService
Simple Spring Rest Service using Cloud Config, Spring Data, PostgreSQL, Hateos

## Dependencies
At least: Java 11 and Maven 3.5

## Build 
mvn package -DskipTests=true

## Run 
### Environment variables
export LOGSTASH=127.0.0.1:5044

### Windows
java -jar target\simpleservice-0.1.1-SNAPSHOT.jar

### Linux (service enabled)
./target/simpleservice-0.1.1-SNAPSHOT.jar start

## Docker build
docker build -t simpleservice:latest . --build-arg JAR_FILE=./target/simpleservice-0.1.1-SNAPSHOT.jar

## Docker publish to github registry
- docker tag serviceconfig:latest docker.pkg.github.com/wlanboy/simpleservice/simpleservice:latest
- docker push docker.pkg.github.com/wlanboy/simpleservice/simpleservice:latest

## Docker Registry repro
- https://github.com/wlanboy/SimpleService/packages/278496

## Docker run PostgreSQL
docker run --name postgres -d -p 5432:5432 -e POSTGRES_PASSWORD=audit -e POSTGRES_USER=audit -e POSTGRES_DB=audit postgres:11.4-alpine

## Docker run service
docker run --name simpleservice -d -p 8201:8201 -v /tmp:/tmp simpleservice:latest
