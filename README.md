A small demo project to (hopefully) showcase that I can still code after taking a break

In hindsight, I wish I had rolled with mongodb (which I have way more experience with) over JPA+postgres as I found JPA even more cumbersome than the last time I worked with it 13y ago, I have no idea how to work with it and be happy with the resulting code but at least I am enjoying the newly found love for mongodb right now

The project consists of a project microservice with 3 resources
* groups - `/v1/groups` - simple crud resource
* projects - `/v1/projects` - slightly more involved resource leveraging cascade inserts/updates/deletes on multiple tables
* notification-subscriptions - `/v1/notification-subscriptions` - configuration of subscriptions to project status changes

The APIs mimic status monitoring, where users can override the status of individual projects and subscribe to status change notifications

(ToBeImplemented) Angular frontend

(ToBeImplemented) Another microservice serving as the authentication/authorization server

### Running via Docker Compose:

`docker compose up --build -d`

starts 2 containers:
* postgres: `postgres:postgres@db:5432/postgres`
* java spring-boot service
  * server - `server:8080`
  * remote debug - port `8000`

### Kubernetes

Prerequisites:
* [Minikube](https://minikube.sigs.k8s.io/docs/start) 
* configure docker registry access 
  ```
  kubectl create secret docker-registry docker-credentials \
        --docker-server={your registry url} \
        --docker-username={your username}} \
        --docker-password={your token}
  ```

Postgres deployment: `kubectl apply .\kube\postgres-deployment.yaml`

Backend deployment: `kubectl apply .\kube\project-service-deployment.yaml` - Hangs on startup currently, after investigating the thread dump, I suspect this to be caused by `@EnableAutoConfiguration`  

Frontend deployment: `kubectl apply .\kube\frontend-deployment.yaml`

### Running locally:

* requires either of the above postgres containers to be running
  * use localhost:5432 + port forwarding `kubectl port-forward postgres-0 5432`
  * or the ip+port returned by `minikube service postgres --url`
* requires env properties to be set
  ```
  env POSTGRES_SERVICE_HOST=localhost
  env POSTGRES_SERVICE_PORT=5432
  env APPLICATION_PORT=9090
  ```
* run the main class

### Testing

You can import the postman collection from `<root>/project-service.postman_collection`

The only env parameter is the server port, by default:
* 8080 when running via docker compose
* Whatever you put as your APPLICATION_PORT env property in case of local runs
* Whatever port you tunnel through in case of minikube