#!/bin/bash

checkStatus() {
  while true; do
    STATUS=$(docker inspect --format "{{json .State.Health.Status }}" $1)
    if [[ $STATUS == "\"healthy\"" ]]; then
      echo "Docker $1 is up and running"
      break
    fi
    echo "Docker $1 status is $STATUS, waiting to be healthy...";
    sleep 3;
  done
}

checkStatus $1