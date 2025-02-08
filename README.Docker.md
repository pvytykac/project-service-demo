### Running via Docker Compose:

docker run: docker compose up -d

starts 2 containers:
* postgres: `postgres:postgres@db:5432/postgres`
* java spring-boot service
  * server - `server:8080`
  * remote debug - port `8000`

### Running locally:

* requires the postgres container above to be running
* requires env properties to be set
  ```
  env POSTGRES_URL=jdbc:postgresql://localhost:5432/postgres
  env APPLICATION_PORT=9090
  ```
* run the main class
  

