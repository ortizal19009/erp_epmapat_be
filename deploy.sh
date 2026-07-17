#!/bin/bash

set -e

echo "=============================="
echo "Iniciando proceso de deploy..."
echo "=============================="

WAR_NAME="erp_epmapat-v0.1.war"
TARGET_DIR="./target"
DEPLOY_DIR="./deploy"
BACKUP_DIR="./backup"

DATE=$(date +"%Y%m%d_%H%M%S")

echo "1. Compilando proyecto..."

./mvnw clean package -DskipTests

echo "2. Creando carpetas necesarias..."

mkdir -p $BACKUP_DIR
mkdir -p $DEPLOY_DIR

echo "3. Respaldando WAR anterior..."

if [ -f "$DEPLOY_DIR/$WAR_NAME" ]; then
    cp $DEPLOY_DIR/$WAR_NAME $BACKUP_DIR/${WAR_NAME}_$DATE
    echo "Backup creado: ${WAR_NAME}_$DATE"
fi

echo "4. Copiando nuevo WAR..."

cp $TARGET_DIR/$WAR_NAME $DEPLOY_DIR/

echo "5. Deteniendo contenedor..."

docker compose --env-file .env.prod down

echo "6. Construyendo imagen..."

docker compose --env-file .env.prod build

echo "7. Iniciando contenedor..."

docker compose --env-file .env.prod up -d --force-recreate

echo "=============================="
echo "Deploy terminado correctamente"
echo "=============================="
