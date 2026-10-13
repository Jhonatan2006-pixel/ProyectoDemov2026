#!/bin/bash
# ============================================================
#  Publica el backend en el servidor de AWS.
#
#  Usarlo cada vez que se cambie el codigo del backend.
#  Desde Git Bash, dentro de la carpeta BackendFinal:
#      bash desplegar_aws.sh
#
#  Compila el proyecto, lo sube al servidor, instala las
#  dependencias y reinicia el servicio. No toca la base de
#  datos ni el archivo .env del servidor.
# ============================================================
set -euo pipefail

SERVIDOR="ubuntu@77.113.138.104"             # usuario@IP fija de la instancia
LLAVE="$HOME/.ssh/aws_proyecto_jhonathan"    # llave SSH autorizada en el servidor
CARPETA="backend-censos"                     # carpeta del backend dentro del servidor
SERVICIO="censo-backend"                     # nombre del servicio en el servidor

SSH_OPCIONES=(-i "$LLAVE" -o IdentitiesOnly=yes -o ConnectTimeout=15)

cd "$(dirname "$0")"

# Carpeta temporal para compilar: no se usa "dist" del proyecto
TEMPORAL="$(mktemp -d)"
trap 'rm -rf "$TEMPORAL"' EXIT

echo "1/4 Compilando el backend..."
npx tsc -p . --outDir "$TEMPORAL/dist"

echo "2/4 Empaquetando..."
cp package.json package-lock.json "$TEMPORAL/"
tar -czf "$TEMPORAL/backend.tgz" -C "$TEMPORAL" dist package.json package-lock.json

echo "3/4 Subiendo al servidor..."
scp "${SSH_OPCIONES[@]}" "$TEMPORAL/backend.tgz" "$SERVIDOR:/tmp/backend.tgz"

echo "4/4 Instalando dependencias y reiniciando el servicio..."
ssh "${SSH_OPCIONES[@]}" "$SERVIDOR" "
    set -e
    mkdir -p ~/$CARPETA
    cd ~/$CARPETA
    rm -rf dist
    tar -xzf /tmp/backend.tgz
    rm /tmp/backend.tgz
    npm ci --omit=dev --no-audit --no-fund
    sudo systemctl restart $SERVICIO
    sleep 4
    systemctl is-active $SERVICIO
"

echo "Listo: backend publicado."
