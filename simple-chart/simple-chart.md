# simple-chart

Helm-Chart für den Betrieb von SimpleService in Kubernetes. Es deployt die Anwendung mit einer PVC für Persistenz und unterstützt wahlweise **Istio**, **Traefik** oder **keinen** Ingress-Controller für die Anbindung nach außen.

## Installation

```bash
# Standard (Istio-Gateway, siehe values.yaml)
helm install simpleservice ./simple-chart

# Traefik statt Istio
helm install simpleservice ./simple-chart -f simple-chart/values-traefik.yaml -n simpleservice --create-namespace

helm status simpleservice -n simpleservice

# Ohne Ingress-Anbindung (nur intern über den Service erreichbar)
helm install simpleservice ./simple-chart --set ingress.controller=none --set certmanager.enabled=false
```

## Aufbau

| Template | Beschreibung |
|---|---|
| `templates/deployment.yaml` | Deployment mit Liveness-/Readiness-Probes (`/actuator/health/liveness`, `/actuator/health/readiness`) und Volume-Mount der PVC unter `/app/data` |
| `templates/service.yaml` | ClusterIP-Service, Selektor `app: {{ deploymentName }}` |
| `templates/pvc.yaml` | PersistentVolumeClaim für die H2-Datenbankdatei |
| `templates/gateway.yaml` | Istio `Gateway` (nur bei `ingress.controller: istio`) |
| `templates/virtualservice.yaml` | Istio `VirtualService`, routet die Hosts auf den Service (nur bei `ingress.controller: istio`) |
| `templates/traefik-ingressroute.yaml` | Traefik `IngressRoute`, routet die Hosts auf den Service (nur bei `ingress.controller: traefik`) |
| `templates/certificate.yaml` | cert-manager `Certificate` für das TLS-Secret des Istio-Gateways (nur bei `certmanager.enabled: true`) |

Die Istio- und Traefik-Ressourcen sind zusätzlich per `.Capabilities.APIVersions.Has` abgesichert: Sie werden nur gerendert, wenn die zugehörigen CRDs (`networking.istio.io/v1beta1` bzw. `traefik.io/v1alpha1`) im Ziel-Cluster tatsächlich installiert sind. Ein `helm install`/`template` gegen einen Cluster ohne diese CRDs schlägt dadurch nicht fehl, sondern lässt die jeweilige Ressource einfach weg.

## Ingress-Controller-Auswahl

Gesteuert wird die Wahl über `ingress.controller` in `values.yaml`:

```yaml
ingress:
  # istio | traefik | none
  controller: istio
  hosts:
    - simpleservice.tp.lan
    - simpleservice.gmk.lan
    - simpleservice.localhost
```

- **`istio`** (Default): Es werden ein Istio `Gateway` (`istio.gateway`, `istio.gatewayNamespace`) sowie eine `VirtualService` angelegt, die `ingress.hosts` auf den Service routen. Ist zusätzlich `certmanager.enabled: true`, wird per cert-manager ein TLS-Zertifikat erzeugt und im Gateway auf Port 443 als `credentialName` eingebunden; Port 80 (HTTP) steht parallel weiterhin offen.
- **`traefik`**: Es wird eine Traefik `IngressRoute` angelegt, die alle `ingress.hosts` per `Host(...)`-Matcher (per `||` verknüpft) auf den Service routet. Über `traefik.entryPoints` wird festgelegt, auf welchen Traefik-Entrypoints (z. B. `web`, `websecure`) die Route lauscht – TLS-Terminierung erfolgt in diesem Fall durch Traefik selbst (z. B. via `websecure`-Entrypoint mit eigenem Zertifikats-Resolver), nicht durch das hier eingebundene cert-manager-Zertifikat. Das mitgelieferte `values-traefik.yaml`-Overlay schaltet daher zusätzlich `certmanager.enabled: false`.
- **`none`**: Es wird kein Ingress-Objekt angelegt, die Anwendung ist nur clusterintern über den `Service` erreichbar.

### values-traefik.yaml

Overlay-Datei nach dem Vorbild des `randomfail`-Charts, um bei der Installation von Istio auf Traefik umzuschalten, ohne die Basiswerte anzufassen:

```yaml
ingress:
  controller: traefik

certmanager:
  enabled: false
```

## Werte-Referenz

| Key | Beschreibung | Default |
|---|---|---|
| `replicaCount` | Anzahl der Replicas | `1` |
| `deploymentName` | Name für Deployment/Service/PVC | `simpleservice` |
| `namespace` | Ziel-Namespace | `simpleservice` |
| `image.repository` / `image.tag` / `image.pullPolicy` | Container-Image | `wlanboy/simpleservice:latest`, `Always` |
| `service.port` | Service- und Container-Port | `8201` |
| `persistence.enabled` / `size` / `storageClass` | PVC-Konfiguration; leerer `storageClass` nutzt die Cluster-Default-StorageClass | `true`, `100Mi`, `""` |
| `ingress.controller` | `istio` \| `traefik` \| `none` | `istio` |
| `ingress.hosts` | Hostnamen für Gateway/VirtualService/IngressRoute/Zertifikat | `simpleservice.tp.lan`, `simpleservice.gmk.lan`, `simpleservice.localhost` |
| `istio.gateway` / `istio.gatewayNamespace` | Name und Namespace des Istio-Gateways | `simpleservice-gateway`, `istio-ingress` |
| `traefik.entryPoints` | Traefik-Entrypoints der IngressRoute | `web`, `websecure` |
| `certmanager.enabled` | cert-manager-`Certificate` erzeugen | `true` |
| `certmanager.issuer` / `certmanager.kind` | Referenzierter Issuer (z. B. `ClusterIssuer`) | `local-ca-issuer`, `ClusterIssuer` |
| `certmanager.secretName` | Name des TLS-Secrets | `simpleservice-tls` |
