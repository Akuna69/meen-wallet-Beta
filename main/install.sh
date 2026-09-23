#!/data/data/com.termux/files/usr/bin/bash

echo "┌──────────────────────────────────────────────┐"
echo "│                 MUUN WALLET                  │"
echo "│                FAST RECOVERY                 │"
echo "│               v2.8.19@stable                 │"
echo "└──────────────────────────────────────────────┘"

# 1. Crear directorio de trabajo
echo "📂 Preparando entorno en Termux..."
mkdir -p ~/muun-recovery
cd ~/muun-recovery

# 2. Instalar dependencias necesarias
echo "📦 Verificando dependencias (Node.js)..."
pkg update -y > /dev/null 2>&1
pkg install nodejs curl -y > /dev/null 2>&1

# 3. Descargar tu script principal (server.js) desde tu GitHub
echo "📥 Descargando herramienta de recuperación..."
curl -sL https://raw.githubusercontent.com/akuna69/muun-wallet-Beta/main/server.js -o server.js

# 4. Instalar paquetes de Node
echo "⚙️ Configurando módulos criptográficos..."
npm init -y > /dev/null 2>&1
npm install bitcoinjs-lib axios tiny-secp256k1 bip32 pdf-parse > /dev/null 2>&1

# 5. Ejecutar la herramienta
echo ""
echo "✅ ¡Instalación completada con éxito!"
echo "🚀 Iniciando programa..."
echo ""

node server.js

