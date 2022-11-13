# CRUD

A demo Spring Boot application for money exchange requests

## Build

The application requires [Docker](https://www.docker.com/) running on host machine because [Testcontainers](https://www.testcontainers.org/) are 
used in tests to run Postgres instance. JAVA_HOME must point to a JDK 17 distribution. For example, SDKMAN can be used for Java version management:

```sh
sdk install java 17.0.5-librca && sdk use java 17.0.5-librca
```

To build the application, use gradle task:

```sh
./gradlew build
```

## Run

Provide Postgres database credentials via environment variables and run gradle task:

```sh
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/postgres"
export SPRING_DATASOURCE_USERNAME=dbuser
export SPRING_DATASOURCE_PASSWORD=dbpassword
./gradlew bootRun
```

The application starts on port 8080. 
Check [Swagger](https://raipc.github.io/crud/) documentation for available API methods


