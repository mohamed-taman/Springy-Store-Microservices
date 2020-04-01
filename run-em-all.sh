#!/usr/bin/env bash
## Author: Mohamed Taman
## version: v1.0

echo -e "Starting [Springy Store] μServices ....\n\
---------------------------------------\n"

function runService(){
   cd "$1" && \
   mvn --quiet spring-boot:run -Dspring-boot.run.jvmArguments="--enable-preview"
}

for dir in `find *-service -maxdepth 0 -type d`
do
    echo -e "Starting [$dir] Microservice.... \n" && \
    runService "$dir" &
done
