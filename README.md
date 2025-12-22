![Java CI with Maven](https://github.com/wlanboy/SimpleService/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master) ![Docker build and publish image](https://github.com/wlanboy/SimpleService/workflows/Docker%20build%20and%20publish%20image/badge.svg)

# SimpleService
Simple Spring Rest Service using Cloud Config, Spring Data, PostgreSQL, Hateos

## Dependencies
At least: Java 21 and Maven 3.9.9

## Build 
mvn package
## Run 

### Windows
java -jar target\simpleservice-0.1.1-SNAPSHOT.jar

### Linux (service enabled)
./target/simpleservice-0.1.1-SNAPSHOT.jar start

## Docker build
docker build -t simpleservice:latest . --build-arg JAR_FILE=./target/simpleservice-0.1.1-SNAPSHOT.jar

## Docker publish to github registry
- docker tag serviceconfig:latest docker.pkg.github.com/wlanboy/simpleservice/simpleservice:latest
- docker push docker.pkg.github.com/wlanboy/simpleservice/simpleservice:latest

## Docker run service
docker run --name simpleservice -d -p 8201:8201 -v /tmp:/tmp -v ${pwd}/data:/data simpleservice:latest
