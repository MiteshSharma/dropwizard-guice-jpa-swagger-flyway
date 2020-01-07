#!/bin/bash

checkStatus() {
  while true; do
    STATUS=$(docker inspect --format "{{json .State.Health.Status }}" $1)
    if [[ $STATUS == "\"healthy\"" ]]; then
      echo "Docker $name is up and running"
      break
    fi
    >&2 echo "Docker $name is unavailable, waiting to start...";
    sleep 3;
  done
}

checkStatus $1