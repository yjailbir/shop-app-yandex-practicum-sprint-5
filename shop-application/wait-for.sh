#!/bin/sh
host="$1"
shift
port="$1"
shift

echo "Waiting for keycloak start at $host:$port..."
until nc -z "$host" "$port"; do
  sleep 1
done

exec "$@"
