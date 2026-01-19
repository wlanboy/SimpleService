# SimpleService
Simple Spring Rest Service using Cloud Config, Spring Data, H2, Hateos to show how simple Blockchains are.

## Dependencies
At least: Java 21 and Maven 3.9.9

## Build
```bash
mvn package
```

## Run 

### Windows
```bash
java -jar target\simpleservice-0.1.1-SNAPSHOT.jar
```

### Linux (service enabled)

```bash
./target/simpleservice-0.1.1-SNAPSHOT.jar start
```

## Docker build

```bash
docker build -t simpleservice:latest .
```

## Docker publish to github registry
- docker tag serviceconfig:latest docker.pkg.github.com/wlanboy/simpleservice/simpleservice:latest
- docker push docker.pkg.github.com/wlanboy/simpleservice/simpleservice:latest

## Docker run service
```bash
docker run --name simpleservice -d -p 8201:8201 -v /tmp:/tmp -v ${pwd}/data:/data simpleservice:latest
```
