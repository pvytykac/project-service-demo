A small demo project to (hopefully) show I can still kinda code after taking a break 

### Running via Docker Compose:

`docker compose up -d`

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

Backend deployment: `kubectl apply .\kube\project-service-deployment.yaml`

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