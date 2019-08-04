# SimpleService
Simple Spring Rest Service using Eureka, Cloud Config and Zipkin

## Dependencies
At least: Java 8 and Maven 3.5

## Build Simple REST HATEOS Service
mvn package -DskipTests=true

## Run Simple REST HATEOS Service
### Environment variables
export DOCKERHOST=192.168.0.100

### Windows
java -jar target\SimpleService.jar

### Linux (service enabled)
./target/SimpleService.jar start

## Docker build
docker build -t simpleservice:latest . --build-arg JAR_FILE=./target/SimpleService.jar

## Docker run
docker run --name simpleservice -d -p 8001:8001 -v /tmp:/tmp simpleservice:latest
