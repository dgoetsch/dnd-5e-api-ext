# DandD 5e API Cache + Extentions

Emulates the DandD 5e api; will cache resources 
from the public 5e servers and cache them loclly.

## Starting the server

First, install [docker](https://docs.docker.com/engine/install/) and [docker-compose](https://docs.docker.com/compose/install/) for your platform

Then launch the provided docker-compose file 

```shell script
$ ./gradlew api:run
```

or

```shell script
$ docker-compose up
```

** Running in docker will cause root owned files to be created under api-resources directory

## Extending the 5e api

The docker-compose file binds to a `api-resources/` relative to this projects
directory.

To add a custom resource, add the json in a file named
`api-resources/[resource_type]/[index].json`.  For example `api-resources/spells/fire-bolt.json`

## Load data & Generate Models

The `generate` project can generate domain models for ui applications based on the json data in the api.

Running `generate` will scrape the entire remote d&d5e api and download the json into `api-resources/`.

Generated files will be in the `generated` project

```shell script
$ ./gradlew generate:run
```

or

```shell script
$ docker-compose -f docker-compose-generate.yml up
```

** Running in docker will cause root owned files to be created under api-resources directory and generated/src/main/kotlin

## Submodules

### JVM Modules (kotlin-java)

#### core

Includes the api and implementation for reading + writing to the backend.
Includes some common utility functions.

#### api

Exposes core as an http server with an api that mimics the D&D api

#### generate

Generates domain models for use in web apps.  Currently supports generating kotlin, planned support for typescript.

### Web submodules (kotlin-js)

#### generated

api models, output from the generate module

#### web

Web a web application for accessing d&d 5e api.  Currently a kotlin server in html; should probably be an angular SPA