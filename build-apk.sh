#!/bin/bash

# Script para compilar DroneScan APK
# Este script debe ejecutarse en una máquina con Android Studio instalado

set -e

echo "========================================="
echo "🚀 DroneScan - Build Script"
echo "========================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar que estamos en el directorio correcto
if [ ! -f "settings.gradle" ]; then
    echo -e "${RED}❌ Error: Este script debe ejecutarse desde la raíz del proyecto${NC}"
    exit 1
fi

# Verificar ANDROID_HOME
if [ -z "$ANDROID_HOME" ]; then
    echo -e "${YELLOW}⚠️  ANDROID_HOME no está configurado${NC}"
    echo ""
    echo "Por favor, configura ANDROID_HOME apuntando a tu Android SDK:"
    echo "  export ANDROID_HOME=/path/to/android/sdk"
    echo "  export PATH=\$PATH:\$ANDROID_HOME/tools:\$ANDROID_HOME/platform-tools"
    echo ""
    echo "O crea un archivo local.properties con:"
    echo "  sdk.dir=/path/to/android/sdk"
    echo ""
    exit 1
fi

echo -e "${GREEN}✅ Android SDK encontrado en: $ANDROID_HOME${NC}"
echo ""

# Limpiar build anterior
echo "🧹 Limpiando builds anteriores..."
./gradlew clean

echo ""
echo "🔨 Compilando DroneScan APK (Debug)..."
echo ""

# Compilar APK Debug
./gradlew assembleDebug

# Verificar si la compilación fue exitosa
if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}=========================================${NC}"
    echo -e "${GREEN}✅ ¡Compilación exitosa!${NC}"
    echo -e "${GREEN}=========================================${NC}"
    echo ""
    echo "📦 El APK ha sido generado en:"
    echo "   app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "📊 Tamaño del APK:"
    du -h app/build/outputs/apk/debug/app-debug.apk
    echo ""
    echo "📱 Para instalar en tu dispositivo:"
    echo "   ./gradlew installDebug"
    echo ""
    echo "O manualmente:"
    echo "   adb install app/build/outputs/apk/debug/app-debug.apk"
    echo ""
else
    echo ""
    echo -e "${RED}=========================================${NC}"
    echo -e "${RED}❌ Error en la compilación${NC}"
    echo -e "${RED}=========================================${NC}"
    echo ""
    echo "Revisa los errores arriba para más detalles."
    exit 1
fi
