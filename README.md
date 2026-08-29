# SimpleService

SimpleService ist ein Spring-Boot-REST-Dienst, der ein einfaches **Blockchain-basiertes Audit-Log** implementiert. Jeder neue Eintrag wird kryptografisch mit seinem Vorgänger verknüpft: Der **SHA-256-Hash** eines Eintrags wird aus `target`, `status`, `counter` und dem Hash des vorherigen Eintrags (`previousHash`) gebildet. Dadurch entsteht eine Kette, bei der jeder Block vom vorherigen abhängt – genau das Grundprinzip, das auch echte Blockchains manipulationssicher macht.

Beim Start der Anwendung wird automatisch – sofern die Tabelle leer ist – ein **Genesis-Block** (`target=GENESIS`, `previousHash="GENESIS"`) erzeugt, der die Kette verankert (siehe `GenesisInitializer`).

# Simple Blockchain

Da jeder Hash vom vorherigen Hash abhängt, lässt sich jede nachträgliche Manipulation eines Eintrags erkennen: Wird ein gespeicherter Datensatz verändert, stimmt sein Hash nicht mehr mit dem neu berechneten Wert überein. Der Dienst bietet dafür einen dedizierten `/verify`-Endpunkt, der sowohl die Existenz des Vorgänger-Hashes als auch die Datenintegrität des Eintrags prüft.

Persistiert werden die Einträge über Spring Data JPA in einer dateibasierten H2-Datenbank (Tabelle `tbl_audit`). Die REST-Antworten sind mit **HATEOAS**-Links angereichert (`AuditLog` erweitert `RepresentationModel`). Für die API-Dokumentation ist **springdoc-openapi** eingebunden (Swagger-UI unter `/swagger-ui/index.html`, OpenAPI-JSON unter `/v3/api-docs`), zusätzlich stehen über **Spring Boot Actuator** und **Micrometer** Health-, Info- und Prometheus-Metrik-Endpunkte zur Verfügung (`/actuator/health`, `/actuator/info`, `/actuator/prometheus`).

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
|                     |  - saveAuditLog()   SHA-256-Hash berechnen
|                     |  - findById()       Einträge lesen
|                     |  - findAll()        paginiert
|                     |  - findByTarget()   suchen
|                     |  - verifyEntry()    Kette validieren
+---------------------+
      |         |
      v         v
+----------+ +----------------+
|AuditMapper| |AuditRepository|  Spring Data JPA
|toEntity() | |findTopByOrder |
|toModel()  | |findAllByTarget|
|generateHash()| |findByHash  |
+----------+ +----------------+
                  |
                  v
          +---------------+
          |  H2 Database  |  Tabelle: tbl_audit
          +---------------+

Blockchain-Kette der Audit-Einträge:

  [Genesis Block]──────►[Entry 1]──────────►[Entry 2]──────────►[Entry N]
  target=GENESIS         target=...           target=...           target=...
  hash=SHA256(...)       previousHash=        previousHash=        previousHash=
  previousHash="GENESIS" hash(Genesis)        SHA256(Entry 1)      SHA256(Entry N-1)
                         hash=SHA256(...)     hash=SHA256(...)     hash=SHA256(...)
```

Jeder Hash wird aus `target + status + counter + previousHash` gebildet (siehe `AuditMapper.generateHash`). `verifyEntry()` prüft dabei zweistufig: zuerst, ob der referenzierte `previousHash` überhaupt in der Kette existiert, dann, ob der gespeicherte Hash noch zu den (unveränderten) Feldern des Eintrags passt.

## Dependencies

Mindestens: Java 25 und Maven 3.9+ (Build läuft über den mitgelieferten `mvnw`/`mvnw.cmd` Wrapper)

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

Der Dienst startet standardmäßig auf Port `8201` (überschreibbar via `PORT`-Umgebungsvariable, siehe `application.yml`). Nach dem Start:

- Swagger-UI: `http://localhost:8201/swagger-ui/index.html`
- Health-Check: `http://localhost:8201/actuator/health`
- Prometheus-Metriken: `http://localhost:8201/actuator/prometheus`

## Docker build

```bash
docker build -t simpleservice:latest .
docker build -f Dockerfile25 -t simpleservice:25 .
docker build -f Dockerfile25Jlink -t simpleservice:jlink .

docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}" | grep "simpleservice"
simpleservice      jlink     296MB
simpleservice      latest    642MB
```

- `Dockerfile` / `Dockerfile25`: Multi-Stage-Build auf Basis von `ubi10/openjdk-25`, inkl. AOT-Processing (Spring AOT) und AppCDS-Archiv (`app.jsa`) für schnelleren Start.
- `Dockerfile21`: einfacher Multi-Stage-Build mit `eclipse-temurin:21-jre-noble` ohne AOT/CDS-Optimierungen.
- `Dockerfile25Jlink`: wie `Dockerfile25`, erzeugt zusätzlich mit `jlink`/`jdeps` ein minimiertes Custom-JRE für ein deutlich kleineres Laufzeit-Image.

## Docker run service

```bash
docker run --rm --name simpleservice -p 8201:8080 -v /tmp:/tmp -v ${pwd}/data:/data simpleservice:latest
#Started AuditApplication in 2.664 seconds

docker run --rm --name simpleservice25 -p 8201:8080 -v /tmp:/tmp -v ${pwd}/data:/data simpleservice:25
#Started AuditApplication in 3.714 seconds

docker run --rm --name simpleservicejl -p 8202:8080 -v /tmp:/tmp -v ${pwd}/data:/data simpleservice:jlink
#Started AuditApplication in 2.681 seconds
```

## Kubernetes / Helm

Für den Betrieb in Kubernetes stehen zwei Varianten bereit:

- `simpleservice-deployment.yaml`: einfaches, statisches Deployment + Service-Manifest.
- `simple-chart/`: Helm-Chart mit konfigurierbaren Werten (Replica-Count, Image, Ingress-Hosts, Persistenz via PVC, optionalem Istio-Gateway sowie cert-manager-Integration für TLS) – siehe `simple-chart/values.yaml`.

```bash
helm install simpleservice ./simple-chart
```

## Multi-Arch-Build

`multiarch-build.sh` baut und published das Image für `linux/amd64` und `linux/arm64` via `docker buildx` unter dem Docker-Hub-Repository `wlanboy/simpleservice`.
