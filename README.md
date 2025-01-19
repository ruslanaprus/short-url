Build the project:
```shell
./gradlew clean build
```
This command will use the gradle wrapper to ensure that the correct version of Gradle is used.

build the app image 
```shell
docker build -t shorturl:1.0.0 .
```

start java app and database containers
```shell
docker-compose --env-file .env up --force-recreate
```