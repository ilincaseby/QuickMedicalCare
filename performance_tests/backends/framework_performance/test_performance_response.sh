#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: $0 <number_of_requests>"
  exit 1
fi

n=$1
url="http://localhost:8080/ping"

time bash -c '
for ((i=1; i<=$1; i++)); do
  curl -s -o /dev/null "$2"
done
' _ "$n" "$url"




