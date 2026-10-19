# Release Manager Technical Challenge

## Introduction
The challenge was done in around 3h and a couple of minutes more :)

### Modules

The project consists of:

- **release-manager:** Our main Spring Boot API
- **deployment-watcher:** A simple python app that reports deployments to our `/deploy` endpoint by reading changes from the docker engine
- **docker-compose.yaml:** Our docker compose services that serve to simulate a cluster

I've opted for Docker Compose due to time constraints and to avoid unnecessary complexity, since it would take much more time to setup a Kubernetes
Cluster with all the different resources that we'll have to create (Deployments, Services, StatefulSets, PVCs etc)

## See it in action

We start by launching our main services (the release-manager app, the postgres database and the watcher python app)

````
docker compose --env-file env/production-1.env up -d release-manager postgres watcher

# We can  also run other environments simultaneously to simulate a multi environment setup if we choose to
# docker compose --env-file env/production-2.env up -d release-manager postgres watcher
# docker compose --env-file env/dev.env up -d release-manager postgres watcher
````


Once everything is running, we can start deploying a couple of sample services (declared in docker-compose.yaml) to our production-1 environment

Let's replay the same scenario from the assignment

````
SERVICE_A_VERSION=1 docker compose --env-file env/production-1.env up -d service-a
SERVICE_B_VERSION=1 docker compose --env-file env/production-1.env up -d service-b
SERVICE_A_VERSION=2 docker compose --env-file env/production-1.env up -d service-a
SERVICE_B_VERSION=1 docker compose --env-file env/production-1.env up -d service-b
````

This should cause our deployment watcher python app to call `/deploy` for each service, thus we can verify whether
our system versioning is correct by testing our `/services?systemVersion=?`endpoint.

`curl http://localhost:8081/services\?systemVersion\=2`

Should return the following response:

```
[
    {
        "name": "service-a",
        "version": 1
    },
    {
        "name": "service-b",
        "version": 1
    }
]
```

As well as

`curl http://localhost:8081/services\?systemVersion\=3`


```
[
    {
        "name": "service-a",
        "version": 2
    },
    {
        "name": "service-b",
        "version": 1
    }
]
```