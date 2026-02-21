# SimpleService

Simple Spring Rest Service using Cloud Config, Spring Data, H2, Hateos to show how simple Blockchains are.

## Description

SimpleService ist ein Spring Boot REST-Dienst, der ein einfaches **Blockchain-basiertes Audit-Log** implementiert. Jeder neue Eintrag wird kryptografisch mit seinem Vorgänger verknüpft: Der BCrypt-Hash eines Eintrags enthält `target`, `status`, `counter` und den Hash des vorherigen Eintrags (`previousHash`). Beim Start wird automatisch ein **Genesis-Block** erzeugt, der die Kette verankert.

Dadurch lässt sich jede nachträgliche Manipulation eines Eintrags erkennen – der Dienst bietet einen dedizierten `/verify`-Endpunkt zur Integritätsprüfung.

### REST API

| Methode | Pfad                   | Beschreibung                               |
|---------|------------------------|--------------------------------------------|
| `POST`  | `/audit`               | Neuen Audit-Eintrag anlegen                |
| `GET`   | `/audit`               | Alle Einträge (paginiert)                  |
| `GET`   | `/audit/{id}`          | Eintrag nach ID abrufen                    |
| `GET`   | `/audit/{id}/verify`   | Integrität eines Eintrags prüfen           |
| `GET`   | `/audit/search?target=`| Einträge nach `target` durchsuchen         |
| `GET`   | `/audit/datetime`      | Aktuelle Serverzeit abrufen                |

## Architecture

```text
  HTTP Client
      |
      v
+---------------------+
|   AuditController   |  @RestController  /audit/**
|                     |  Validierung, HTTP-Routing
+---------------------+
      |
      v
+---------------------+
|    AuditService     |  @Service
|                     |  - saveAuditLog()   BCrypt-Hash berechnen
|                     |  - findById()       Einträge lesen
|                     |  - findAll()        paginiert
|                     |  - findByTarget()   suchen
|                     |  - verifyEntry()    Kette validieren
+---------------------+
      |         |
      v         v
+----------+ +-------------+
|AuditMapper| |AuditRepository|  Spring Data JPA
|toEntity() | |findTopByOrder |
|toModel()  | |findAllByTarget|
+----------+ +-------------+
                  |
                  v
          +---------------+
          |  H2 Database  |  Tabelle: audit_log
          +---------------+

Blockchain-Kette der Audit-Einträge:

  [Genesis Block]──────►[Entry 1]──────────►[Entry 2]──────────►[Entry N]
  target=GENESIS         target=...           target=...           target=...
  hash="GENESIS"         previousHash=        previousHash=        previousHash=
  previousHash="NONE"    "GENESIS"            BCrypt(Entry 1)      BCrypt(Entry N-1)
                         hash=BCrypt(...)      hash=BCrypt(...)     hash=BCrypt(...)
```

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
docker build -f Dockerfile25Jlink -t simpleservice:jlink .

docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}" | grep "simpleservice"
simpleservice            jlink     261MB
simpleservice            latest    512MB
```

## Docker run service

```bash
docker run --rm --name simpleservice -p 8201:8201 -v /tmp:/tmp -v ${pwd}/data:/data simpleservice:latest
docker run --rm --name simpleservice -p 8201:8201 -v /tmp:/tmp -v ${pwd}/data:/data simpleservice:jlink
```
