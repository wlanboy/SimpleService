# SimpleService
Simple Spring Rest Service using Eureka, Cloud Config and Zipkin

## Dependencies
At least: Java 8 and Maven 3.5

## Build Simple REST HATEOS Service
mvn package -DskipTests=true

## Run Simple REST HATEOS Service
### Environment variables
#### EUREKA_ZONE 
Default value: http://127.0.0.1:8761/eureka/
Defining all available Eureka Instances.

### Windows
java -jar target\SimpleService.jar

### Linux (service enabled)
./target/SimpleService.jar start

## Docker build
docker build -t simpleservice:latest . --build-arg JAR_FILE=./target/SimpleService.jar

## Docker run
export DOCKERHOST=192.168.0.100
docker run --name simpleservice -d -p 8888:8888 -v /tmp:/tmp -e EUREKA_ZONE=http://$DOCKERHOST:8761/eureka/ simpleservice:latest
