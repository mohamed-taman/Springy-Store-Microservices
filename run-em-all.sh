#!/usr/bin/env bash
## Author: Mohamed Taman
## version: v4.2
#### FIXME need to start up infra structure database and message services
echo -e "Starting [Springy Store] μServices ....\n\
---------------------------------------\n"

function runService(){
   ./mvnw --quiet spring-boot:run -Dspring-boot.run.jvmArguments="--enable-preview" -pl $1
}

for dir in `find  store-services/*-service -maxdepth 0 -type d`
do
    echo -e "Starting [$dir] μService.... \n" && \
    runService "$dir" &
done
