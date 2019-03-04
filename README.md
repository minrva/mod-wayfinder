# Mod-Wayfinder

Copyright (C) 2016-2019 The Open Library Foundation

This software is distributed under the terms of the Apache License,
Version 2.0. See the file "[LICENSE](LICENSE)" for more information.

## Introduction

Mod-Wayfinder is an Okapi module that provides the webservices for a wayfinder.

## Configure Mod-Wayfinder

None.

## Install Mod-Wayfinder

```bash
mvn clean install
```

## Install Mod-Wayfinder Docker Image

```bash
docker container prune
docker rmi mod-wayfinder
docker build -t mod-wayfinder .
```

## Deploy as Stand-Alone Application

### Java Verticle

```bash
cd ~/Desktop/folio/bl/mod-wayfinder
java -jar target/mod-wayfinder-fat.jar embed_postgres=true
```

### Docker Container

```bash
cd ~/Desktop/folio/bl/mod-wayfinder
docker run -t -i -p 8081:8081 mod-wayfinder embed_postgres=true
```

### cURL Usage

1. Create tenant-module databasae (i.e. enable module).

    ```bash
    curl -i -w '\n' -X POST -H 'Content-type: application/json' \
        -H 'X-Okapi-Token: dummyJwt.eyJzdWIiOiJzZWIiLCJ0ZW5hbnQiOm51bGx9.sig' \
        -H 'X-Okapi-Tenant: diku' http://localhost:8081/_/tenant
    ```

## Deploy as Okapi Module

### Full: Postgresql Server, All Modules

1. Set-up environment (see 0-installation docs).

    ```bash
    sudo ifconfig lo0 alias 10.0.2.15
    brew services restart postgresql@9.6
    open -a Docker
    # wait for Docker to start before running next cmd
    docker run -d -v /var/run/docker.sock:/var/run/docker.sock -p 127.0.0.1:4243:4243 bobrik/socat TCP-LISTEN:4243,fork UNIX-CONNECT:/var/run/docker.sock
    ```

1. Deploy Okapi

    ```bash
    cd ~/Desktop/folio
    java \
        -Dstorage=postgres \
        -Dpostgres_host=localhost \
        -Dpostgres_port=5432 \
        -Dpostgres_user=okapi \
        -Dpostgres_password=okapi25 \
        -Dpostgres_database=okapi \
        -Dhost=10.0.2.15 \
        -Dport=9130 \
        -Dport_start=9131 \
        -Dport_end=9661 \
        -DdockerURL=http://localhost:4243 \
        -Dokapiurl=http://10.0.2.15:9130 \
        -jar bl/okapi/okapi-core/target/okapi-core-fat.jar dev
    ```

1. *Only Once*: Deploy Okapi modules.

    ```bash
    source activate folio
    python ~/Desktop/folio/bl/dev-ops/deploy_modules.py
    source deactivate folio
    ```

1. Change to mod-wayfinder directory.

    ```bash
    cd ~/Desktop/folio/bl/mod-wayfinder
    ```

1. *Only Once*: Register `mod-wayfinder`.

    ```bash
    curl -w '\n' -X POST -D -   \
        -H "Content-type: application/json"   \
        -d @target/ModuleDescriptor.json \
        http://localhost:9130/_/proxy/modules
    curl http://localhost:9130/_/proxy/modules
    ```

1. *Only Once*: **Either** deploy `mod-wayfinder` as a **Docker container**.

    ```bash
    curl -w '\n' -D - -s \
        -X POST \
        -H "Content-type: application/json" \
        -d @target/DockerDeploymentDescriptor.json  \
        http://localhost:9130/_/discovery/modules
    curl -i -w '\n' -X GET http://localhost:9130/_/discovery/modules
    ```

1. *Only Once*: **Or** deploy `mod-wayfinder` as a **Java application**.

    ```bash
    curl -w '\n' -D - -s \
        -X POST \
        -H "Content-type: application/json" \
        -d @target/DeploymentDescriptor.json  \
        http://localhost:9130/_/discovery/modules
    curl -i -w '\n' -X GET http://localhost:9130/_/discovery/modules
    ```

1. *Only Once*: Enable `mod-wayfinder` for `diku` tenant.

    ```bash
    curl -w '\n' -X POST -D -   \
        -H "Content-type: application/json"   \
        -d @target/EnableDescriptor.json \
        http://localhost:9130/_/proxy/tenants/diku/modules
    curl http://localhost:9130/_/proxy/tenants/diku/modules
    ```

1. Request `wayfinders` through `mod-wayfinder`.

    ```bash
    # LOGIN and get x-okapi-token and use it for the next requests
    curl -i -w '\n' -X POST -H 'X-Okapi-Tenant: diku' \
        -H "Content-type: application/json" \
        -d '{"username": "diku_admin", "password": "admin"}' \
        http://localhost:9130/authn/login

    # GET shelves
    curl -i -w '\n' -X GET -H 'X-Okapi-Tenant: diku' \
        -H 'X-Okapi-Token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaWt1X2FkbWluIiwidXNlcl9pZCI6IjM4YmNlODAyLTA3MGItNWExNC1iMGNlLTQzMjgwZjVmNjQyMiIsImlhdCI6MTU1MTQyMDA4OCwidGVuYW50IjoiZGlrdSJ9.azRvMvNNDEWr1qKZ2eaEFLicRcOBblO41SB1j5JKyh8' \
        http://localhost:9130/shelves

    # GET shelf that contains book's Library of Congress call number
    # query = (lowerBound <= "TX773 .E334 2004" and upperBound >= "TX773 .E334 2004")
    # result should return shelf 52 similar to,
    # https://minrva.library.illinois.edu/index.html#loc/uiu_undergrad/apple/uiu_5999841/Book
    curl -i -w '\n' -X GET \
        -H 'Content-type: application/json' \
        -H 'X-Okapi-Token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaWt1X2FkbWluIiwidXNlcl9pZCI6IjM4YmNlODAyLTA3MGItNWExNC1iMGNlLTQzMjgwZjVmNjQyMiIsImlhdCI6MTU1MTQyMDA4OCwidGVuYW50IjoiZGlrdSJ9.azRvMvNNDEWr1qKZ2eaEFLicRcOBblO41SB1j5JKyh8' \
        -H 'X-Okapi-Tenant: diku' 'http://localhost:9130/shelves?query=%28lowerBound%20%3C%3D%20%22TX773%20.E334%202004%22%20and%20upperBound%20%3E%3D%20%22TX773%20.E334%202004%22%29'

    # POST shelves (shelf1.json and shelf2.json)
    curl -i -w '\n' -X POST -H 'Content-type: application/json' \
        -H 'X-Okapi-Token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaWt1X2FkbWluIiwidXNlcl9pZCI6IjM4YmNlODAyLTA3MGItNWExNC1iMGNlLTQzMjgwZjVmNjQyMiIsImlhdCI6MTU1MDk3MDQwMiwidGVuYW50IjoiZGlrdSJ9.aundQ-DeuSDnYB4K45yNmW30yVLh4PafYRLC3YQLylQ' \
        -H 'X-Okapi-Tenant: diku' \
        -d @ramls/examples/shelf1.json http://localhost:9130/shelves

    curl -i -w '\n' -X POST -H 'Content-type: application/json' \
        -H 'X-Okapi-Token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaWt1X2FkbWluIiwidXNlcl9pZCI6IjM4YmNlODAyLTA3MGItNWExNC1iMGNlLTQzMjgwZjVmNjQyMiIsImlhdCI6MTU1MDk3MDQwMiwidGVuYW50IjoiZGlrdSJ9.aundQ-DeuSDnYB4K45yNmW30yVLh4PafYRLC3YQLylQ' \
        -H 'X-Okapi-Tenant: diku' \
        -d @ramls/examples/shelf2.json http://localhost:9130/shelves
    ```

1. *Optional*: Deploy Stripes Platform

    ```bash
    cd ~/Desktop/folio/ui/platform-complete
    yarn start
    ```

1. Tear-down
    `ctrl + c` out of okapi and stripes.

    ```bash
    docker kill $(docker ps -q)
    docker container prune
    brew services stop postgresql@9.6
    sudo ifconfig lo0 -alias 10.0.2.15
    ```

Note: Launch descriptor has a path relative to the directory that `java -jar okapi-core-fat.jar` was executed in (likely `~/Desktop/folio`).

## Reference

1. Unregister mod-wayfinder from Okapi gateway.

    ```bash
    # undeploy mod-wayfinder
    curl -w '\n' -X DELETE  -D - http://localhost:9130/_/discovery/modules/mod-wayfinder-1.0.0/10.0.2.15-9144
    # disable mod-wayfinder for diku
    curl -w '\n' -X DELETE  -D - http://localhost:9130/_/proxy/tenants/diku/modules/mod-wayfinder-1.0.0
    # unregister mod-wayfinder
    curl -w '\n' -X DELETE  -D - http://localhost:9130/_/proxy/modules/mod-wayfinder-1.0.0
    ```

1. List registered modules.

    ```bash
    # list deployed modules
    curl -i -w '\n' -X GET http://localhost:9130/_/discovery/modules
    # list modules enabled for diku user
    curl -i -w '\n' -X GET http://localhost:9130/_/proxy/tenants/diku/modules
    # list modules registered with okapi
    curl -i -w '\n' -X GET http://localhost:9130/_/proxy/modules
    ```
