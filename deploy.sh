#!/usr/bin/env bash

cd max-altitude; mvn clean package; cd ..

JOBMANAGER_CONTAINER=$(docker ps --filter name=jobmanager --format={{.ID}})
docker cp "./max-altitude/target/max-altitude-0.1.jar" "$JOBMANAGER_CONTAINER":/job.jar
docker exec -t -i "$JOBMANAGER_CONTAINER" flink run /job.jar
