# 🔨 Instrucciones de Compilación - DroneScan V9

## ⚡ Compilación Rápida

### Prerrequisitos
- ✅ Android Studio instalado
- ✅ JDK 17 configurado
- ✅ Android SDK 34 instalado
- ✅ Conexión a Internet (para descargar dependencias)

---

## 🚀 Método 1: Android Studio (Recomendado)

### Paso 1: Abrir Proyecto
```
1. Abrir Android Studio
2. File > Open
3. Seleccionar carpeta: DroneScan_V9
4. Esperar sincronización de Gradle (automática)
```

### Paso 2: Clean Project
```
Build > Clean Project
```

### Paso 3: Rebuild Project
```
Build > Rebuild Project
```

### Paso 4: Generar APK Debug
```
Build > Build Bundle(s) / APK(s) > Build APK(s)
```

**APK generado en:**
```
app/build/outputs/apk/debug/DroneScan_V9-debug-v1.0.apk
```

### Paso 5: Instalar en Dispositivo

**Opción A - Desde Android Studio:**
```
1. Conectar teléfono Android via USB
2. Habilitar "Depuración USB" en el teléfono
3. Run > Run 'app'
```

**Opción B - Manualmente:**
```bash
adb install -r app/build/outputs/apk/debug/DroneScan_V9-debug-v1.0.apk
```

---

## 💻 Método 2: Línea de Comandos

### Compilación Completa

```bash
# Navegar al directorio del proyecto
cd DroneScan_V9

# Limpiar proyecto
./gradlew clean

# Compilar APK debug
./gradlew assembleDebug

# APK generado en:
# app/build/outputs/apk/debug/DroneScan_V9-debug-v1.0.apk
```

### Compilar e Instalar Directamente

```bash
# Conectar dispositivo Android via USB
# Habilitar Depuración USB en el teléfono

# Compilar e instalar en un solo comando
./gradlew clean installDebug
```

### Compilar APK Release (Firmado)

```bash
# Requiere configuración de keystore
./gradlew assembleRelease

# APK generado en:
# app/build/outputs/apk/release/DroneScan_V9-release-v1.0.apk
```

---

## 🔍 Verificación Post-Compilación

### 1. Verificar que el APK Existe

```bash
ls -lh app/build/outputs/apk/debug/DroneScan_V9-debug-v1.0.apk
```

**Output esperado:**
```
-rw-r--r-- 1 user user 45M Oct 28 12:00 DroneScan_V9-debug-v1.0.apk
```

### 2. Verificar Contenido del APK

```bash
# Ver estructura del APK
unzip -l app/build/outputs/apk/debug/DroneScan_V9-debug-v1.0.apk | grep -i dji | head -10
```

**Debe incluir:**
```
dji/v5/manager/SDKManager.class
dji/v5/common/...
dji/v5/network/...
```

### 3. Verificar Package Name

```bash
aapt dump badging app/build/outputs/apk/debug/DroneScan_V9-debug-v1.0.apk | grep package
```

**Output esperado:**
```
package: name='com.dronescan.msdksample' versionCode='1' versionName='1.0'
```

---

## 🐛 Solución de Problemas de Compilación

### Error: "SDK location not found"

**Solución:**
```bash
# Crear archivo local.properties
echo "sdk.dir=/path/to/Android/Sdk" > local.properties

# En Windows:
# echo sdk.dir=C:\\Users\\YourUser\\AppData\\Local\\Android\\Sdk > local.properties

# En macOS/Linux:
# echo sdk.dir=$HOME/Library/Android/sdk > local.properties
# o
# echo sdk.dir=$HOME/Android/Sdk > local.properties
```

### Error: "Could not resolve dependencies"

**Solución:**
```bash
# Limpiar caché de Gradle
./gradlew clean build --refresh-dependencies

# Si persiste, eliminar caché manualmente
rm -rf ~/.gradle/caches/
./gradlew clean build
```

### Error: "Java version mismatch"

**Solución:**
```bash
# Verificar versión de Java
java -version

# Debe ser JDK 17
# Si no, configurar en Android Studio:
# File > Project Structure > SDK Location > JDK Location
```

### Error: "Gradle sync failed"

**Solución:**
```bash
# En Android Studio:
File > Invalidate Caches > Invalidate and Restart

# O desde línea de comandos:
./gradlew clean --refresh-dependencies
```

### Error: "Build Tools version not found"

**Solución:**
```
1. Abrir Android Studio
2. Tools > SDK Manager
3. SDK Tools tab
4. Instalar "Android SDK Build-Tools 34"
5. Sync project
```

---

## 📱 Instalación en Dispositivo

### Prerrequisitos en el Teléfono

1. **Habilitar Depuración USB:**
   ```
   Settings > About Phone > Tap "Build Number" 7 times
   Settings > Developer Options > Enable "USB Debugging"
   ```

2. **Permitir Instalación de Apps Desconocidas:**
   ```
   Settings > Security > Enable "Unknown Sources"
   ```

3. **Conectar via USB al PC**

### Verificar Conexión ADB

```bash
# Listar dispositivos conectados
adb devices

# Output esperado:
# List of devices attached
# ABC123XYZ    device
```

### Instalar APK

```bash
# Desinstalar versión anterior (si existe)
adb uninstall com.dronescan.msdksample

# Instalar nueva versión
adb install -r app/build/outputs/apk/debug/DroneScan_V9-debug-v1.0.apk
```

### Verificar Instalación

```bash
# Listar apps instaladas
adb shell pm list packages | grep dronescan

# Output esperado:
# package:com.dronescan.msdksample
```

---

## 🧪 Testing Post-Instalación

### 1. Preparar Hardware

```
✅ DJI Neo - Encendido
✅ RC-N3 - Encendido y conectado al drone
✅ Teléfono - Conectado al RC via USB
✅ Internet - Disponible (primera vez)
```

### 2. Monitorear Logs

```bash
# Limpiar logs
adb logcat -c

# Ver logs en tiempo real
adb logcat -s DroneScan:* DJISDKHelper:* AndroidRuntime:E
```

### 3. Abrir App

```
1. Abrir "DroneScan" en el teléfono
2. Otorgar permisos cuando se soliciten:
   - Ubicación
   - Cámara
   - USB
   - Bluetooth
```

### 4. Verificar Inicialización

**Logs esperados:**
```
DJISDKHelper: Initializing DJI Mobile SDK...
DJISDKHelper: Init Process: INITIALIZE_COMPLETE
DJISDKHelper: DJI SDK Registration Success
DJISDKHelper: Product Connected: [Product ID]
```

### 5. Verificar UI

**Debe mostrar:**
- ✅ Estado: "SDK Registered"
- ✅ Drone Status: "Connected"
- ✅ Video feed visible
- ✅ GPS coordinates actualizándose

---

## 📊 Tiempos de Compilación Estimados

| Acción | Primera Vez | Subsecuente |
|--------|-------------|-------------|
| **Sync Gradle** | 2-5 min | 10-30 seg |
| **Clean Build** | 3-7 min | 1-2 min |
| **Incremental Build** | - | 20-60 seg |
| **Install APK** | 10-30 seg | 10-30 seg |

*Tiempos pueden variar según hardware y conexión a Internet*

---

## 📦 Tamaño del APK

| Build Type | Tamaño Aproximado |
|------------|-------------------|
| **Debug** | 40-50 MB |
| **Release (sin minify)** | 40-50 MB |
| **Release (con minify)** | 25-35 MB |

---

## 🔧 Comandos Útiles

### Información del Proyecto
```bash
# Ver versión de Gradle
./gradlew --version

# Ver todas las tareas disponibles
./gradlew tasks

# Ver dependencias
./gradlew dependencies
```

### Limpieza
```bash
# Limpiar build
./gradlew clean

# Limpiar caché de Gradle
./gradlew cleanBuildCache
```

### Compilación
```bash
# Compilar debug
./gradlew assembleDebug

# Compilar release
./gradlew assembleRelease

# Compilar ambos
./gradlew assemble
```

### Instalación
```bash
# Instalar debug
./gradlew installDebug

# Desinstalar
./gradlew uninstallAll
```

### Testing
```bash
# Ejecutar tests unitarios
./gradlew test

# Ejecutar tests de instrumentación
./gradlew connectedAndroidTest
```

---

## 📋 Checklist Pre-Release

Antes de generar un APK para distribución:

- [ ] Todas las funciones testeadas
- [ ] Sin errores en logs
- [ ] ProGuard rules verificadas
- [ ] App Key DJI correcto en Manifest
- [ ] Package name correcto: `com.dronescan.msdksample`
- [ ] Version code incrementado
- [ ] Version name actualizado
- [ ] APK firmado con keystore de release
- [ ] Testing en múltiples dispositivos
- [ ] Documentación actualizada

---

## 🎯 Siguiente Paso

Después de compilar exitosamente:

1. **Instalar en teléfono de prueba**
2. **Conectar hardware DJI (Neo + RC-N3)**
3. **Verificar conexión al drone**
4. **Probar video streaming**
5. **Probar escaneo de códigos**
6. **Reportar cualquier limitación encontrada**

---

## 📞 Soporte

Si encuentras problemas durante la compilación:

1. Revisar [NOCLASSDEFFOUNDERROR_FIX.md](NOCLASSDEFFOUNDERROR_FIX.md)
2. Revisar [DJI_NEO_COMPATIBILITY.md](DJI_NEO_COMPATIBILITY.md)
3. Verificar logs de compilación
4. Consultar DJI Forum: https://forum.dji.com/

---

**Última Actualización:** 28 de Octubre, 2024  
**Versión:** DroneScan V9 (1.0)  
**SDK:** DJI Mobile SDK V5.8.0

