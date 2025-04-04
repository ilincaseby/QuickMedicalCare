#!/bin/bash

var1="$1"

if [ "$var1" == "start" ]; then
    docker-compose -f spring_database/docker-compose.yaml up -d
    docker-compose -f authentication_database/docker-compose.yml up -d
fi

if [ "$var1" == "stop" ]; then
    docker-compose -f spring_database/docker-compose.yaml down
    docker-compose -f authentication_database/docker-compose.yml down
fi