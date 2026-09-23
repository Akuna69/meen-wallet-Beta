#!/usr/bin/env bash

# Salir inmediatamente si algún comando falla y mostrar el número de línea del error
set -e
trap 'echo "❌ Error en libwallet-android.sh en la línea $LINENO"' ERR

repo_root=$(git rev-parse --show-toplevel)
build_dir="$repo_root/libwallet/.build"

# OSS project has a different folder libwallet aar, so we receive it as param
libwallet="$1"
if [[ ! -s "$1" ]]; then
    libwallet="$repo_root/android/libwallet/libs/libwallet.aar"
fi

cd "$repo_root/libwallet"

mkdir -p "$(dirname "$libwallet")"

# Create the cache folders
mkdir -p "$build_dir/android"
mkdir -p "$build_dir/pkg"

export GOCACHE="$build_dir/android"
export GOPATH="$build_dir/pkg"

# Verificar que Go esté disponible
if ! command -v go &> /dev/null; then
    echo "❌ Error: Go no está instalado o no se encuentra en el PATH."
    exit 1
fi

# Install and setup gomobile on demand (no-op if already installed and up-to-date)
if [[ -f "$repo_root/tools/bootstrap-gomobile.sh" ]]; then
    . "$repo_root/tools/bootstrap-gomobile.sh"
else
    echo "❌ Error: No se encontró el script bootstrap-gomobile.sh en tools/"
    exit 1
fi

# gomobile bind generates the src-android-* directories several times, leading to fail with:
# /tmp/go-build.../b001/exe/gomobile: mkdir $GOCACHE/src-android-arm64: file exists
rm -rf "$GOCACHE"/src-android-* 2>/dev/null \
  || echo "No src-android-* directories found in GOCACHE."

# Set linker flags for 16KB page alignment required by Android targetSdk 35+
export CGO_LDFLAGS="-Wl,-z,max-page-size=16384 -Wl,-z,common-page-size=16384"

# Auto-detectar la ruta válida del NDK en GitHub Actions
if [ -z "$ANDROID_NDK_HOME" ] || [ ! -d "$ANDROID_NDK_HOME" ]; then
    if [ -n "$ANDROID_NDK_LATEST_HOME" ] && [ -d "$ANDROID_NDK_LATEST_HOME" ]; then
        export ANDROID_NDK_HOME="$ANDROID_NDK_LATEST_HOME"
    elif [ -n "$ANDROID_HOME" ] && [ -d "$ANDROID_HOME/ndk-bundle" ]; then
        export ANDROID_NDK_HOME="$ANDROID_HOME/ndk-bundle"
    else
        ndk_found=$(find "${ANDROID_HOME:-/usr/local/lib/android/sdk}/ndk" -maxdepth 1 -mindepth 1 -type d 2>/dev/null | tail -n 1)
        if [ -n "$ndk_found" ]; then
            export ANDROID_NDK_HOME="$ndk_found"
        fi
    fi
fi

export ANDROID_NDK_ROOT="$ANDROID_NDK_HOME"
echo "📌 Usando NDK en: $ANDROID_NDK_HOME"

echo "🚀 Iniciando gomobile bind..."

# Se ejecuta gomobile bind apuntando únicamente a la raíz del paquete Go (.)
set +e
go run golang.org/x/mobile/cmd/gomobile bind \
    -target=android/arm64,android/amd64 \
    -o "$libwallet" \
    -androidapi 21 \
    -trimpath -ldflags="-buildid=" -v \
    . ./app_provided_data ./newop

st=$?
set -e

if [ $st -eq 0 ]; then
    echo "✅ gomobile compilado exitosamente en $libwallet"
else
    echo "❌ Error: gomobile bind falló con el código de salida $st"
fi

exit $st
