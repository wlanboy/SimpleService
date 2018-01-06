#!/bin/bash
set -e

mvn package -DskipTests=true
docker build -t simpleservice:latest . --build-arg JAR_FILE=./target/SimpleService.jar
docker run --name simpleservice -d -p 8001:8001 --link serviceregistry:serviceregistry -v /tmp:/tmp simpleservice:latest
