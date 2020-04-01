#!/usr/bin/env bash
## Author: Mohamed Taman
## version: v1.0

echo -e "Stopping [Springy Store] μServices ....\n\
---------------------------------------\n"
for port in 9080 9081 9082 9083
do
    echo "Stopping Microservice at port $port ...."
    curl -X POST localhost:${port}/actuator/shutdown
    echo -e "\nMicroservice at port ${port} stopped successfully .... \n"
done
