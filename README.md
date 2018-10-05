# Mod-Wayfinder

Copyright (C) 2018 The Open Library Foundation

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
