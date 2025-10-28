# Configuración de GitHub Actions + Firebase Test Lab

Este proyecto incluye CI/CD automatizado con GitHub Actions y pruebas en dispositivos reales con Firebase Test Lab.

## 📋 Configuración Necesaria

### 1. Firebase Console

1. **Crear Proyecto Firebase:**
   ```bash
   # Ir a: https://console.firebase.google.com/
   # Crear nuevo proyecto: dronescan-v9
   ```

2. **Habilitar Test Lab:**
   - En Firebase Console → Test Lab
   - Activar API de Test Lab

3. **Crear Service Account:**
   ```bash
   # Firebase Console → Project Settings → Service Accounts
   # Click "Generate new private key"
   # Descargar el archivo JSON
   ```

### 2. GitHub Secrets

Configurar en GitHub: `Settings → Secrets and variables → Actions`

**Secret requerido:**
- `FIREBASE_SERVICE_ACCOUNT`: Contenido completo del JSON de la service account

### 3. Google Cloud Storage

Crear bucket para resultados de tests:
```bash
# Nombre del bucket: dronescan-v9-test-results
# Región: us-central1
```

## 🚀 Workflow

### Eventos que Disparan el CI:

- **Push a `main` o `develop`**: Compila + Firebase Test Lab
- **Pull Request a `main`**: Solo compila y tests unitarios

### Jobs:

#### 1. Build (Siempre se ejecuta)
- ✅ Compila APK Debug
- ✅ Compila APK de Test
- ✅ Ejecuta tests unitarios
- ✅ Guarda artefactos

#### 2. Firebase Test Lab (Solo en push a main)
- ✅ Ejecuta tests en Pixel 2 (Android 11)
- ✅ Ejecuta tests en Pixel 6 (Android 13)
- ✅ Guarda resultados por 30 días

## 📱 Tests Incluidos

### Tests Unitarios (JVM)
- `BasicUnitTest.kt`: Tests básicos sin dependencias Android

### Tests de Instrumentación (Android)
- `BasicInstrumentedTest.kt`:
  - ✅ Verifica que la app se instala
  - ✅ Verifica que la MainActivity inicia
  - ✅ Verifica que el SDK no crashea
  - ✅ Verifica elementos UI básicos

## 🔧 Comandos Locales

### Ejecutar tests unitarios:
```bash
./gradlew testDebugUnitTest
```

### Ejecutar tests de instrumentación:
```bash
./gradlew connectedDebugAndroidTest
```

### Compilar APKs para Firebase:
```bash
./gradlew assembleDebug assembleDebugAndroidTest
```

### Ver reportes:
```bash
# Tests unitarios
open app/build/reports/tests/testDebugUnitTest/index.html

# Tests instrumentados
open app/build/reports/androidTests/connected/index.html
```

## 📊 Resultados

Los resultados están disponibles en:
- **GitHub Actions**: Tab "Actions" del repositorio
- **Artefactos**: Descargables por 7-30 días
- **Firebase Console**: Test Lab → Historial de tests

## ⚠️ Limitaciones Actuales

1. **Sin Drone Real**: Los tests no requieren hardware DJI
2. **Tests Básicos**: Solo verifican que la app no crashea
3. **Mock Necesario**: Para tests más avanzados, necesitarás mockear el DJI SDK

## 🔄 Mejoras Futuras

- [ ] Tests con mock del DJI SDK
- [ ] Tests de UI con Espresso
- [ ] Tests de integración del barcode scanner
- [ ] Tests de performance
- [ ] Screenshot testing

## 📝 Notas

- **Costo**: Firebase Test Lab tiene cuota gratuita limitada
- **Tiempo**: Cada run toma ~10-15 minutos
- **Dispositivos**: Se pueden agregar más dispositivos en el workflow
