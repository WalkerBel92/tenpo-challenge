#!/bin/bash
set -e

echo "Esperando a que Redis esté disponible..."
until redis-cli -h redis -p 6379 ping | grep PONG; do
  echo "Redis no está listo, esperando..."
  sleep 2
done
echo "Redis está disponible, iniciando la aplicación..."

exec java -jar app.jar
