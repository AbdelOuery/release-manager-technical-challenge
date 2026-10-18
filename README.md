# release-manager-technical-challenge

## Introduction
The challenge was done in around 3h and a couple of minutes more :)

### Modules

The project is comprised of:

- **release-manager:** Our main API
- **deployment-watcher:** A simple python app that reports deployments to our `/deploy` endpoint by communicating with the docker engine
- **docker-compose.yaml:** Our docker compose services that serve to simulate a cluster

## See it in action

We start by launching our main services (the release-manager app, postgres db and the watcher python app)

``
docker compose --env-file env/production-1 up -d release-manager postgres watcher
``

Now we can start deploying a couple of sample services (declared in docker-compose.yaml) in our production-1 environment

We'll have to feed in the appropriate environment variable specifying the version for our service

````
ENV=SERVICE_A_VERSION=1 docker compose --env-file env/production-1 up -d service-a
ENV=SERVICE_B_VERSION=1 docker compose --env-file env/production-1 up -d service-b
ENV=SERVICE_A_VERSION=2 docker compose --env-file env/production-1 up -d service-a
````

The last command will cause our deployment watcher python app to call `/deploy` for each service, thus we can verify wether
our system version is correct using a simple curl `curl http://localhost:8081/services\?systemVersion\=2 -X GET` which should respond by listing
the deployed services and their respective versions 

