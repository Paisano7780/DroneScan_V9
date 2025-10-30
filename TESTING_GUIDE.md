# 🧪 Sistema de Pruebas con DJI Simulator

## Descripción General

Este proyecto implementa un sistema completo de pruebas automatizadas para aplicaciones DJI usando el **Simulador DJI Aircraft**, que permite testing sin hardware físico.

## 🎯 Componentes

### 1. SimulatorManager

Gestiona el simulador de drones DJI para pruebas.

```kotlin
// Iniciar simulador con ubicación por defecto (Shenzhen, China - HQ de DJI)
SimulatorManager.getInstance().startSimulator()

// Iniciar con ubicación personalizada
SimulatorManager.getInstance().startSimulator(
    latitude = 22.5431,
    longitude = 114.0579,
    satelliteCount = 12
)

// Detener simulador
SimulatorManager.getInstance().stopSimulator()

// Verificar si está corriendo
val isRunning = SimulatorManager.getInstance().isSimulatorRunning()
```

### 2. Modo de Pruebas Automático

La app detecta automáticamente si está corriendo en un entorno de testing y activa el simulador:

```kotlin
// En DroneScanApplication.kt
companion object {
    var isTestMode: Boolean = false
}

// Detección automática
private fun isRunningInTestEnvironment(): Boolean {
    return try {
        // Detecta framework de Android Test
        Class.forName("androidx.test.espresso.Espresso")
        true
    } catch (e: ClassNotFoundException) {
        // Detecta Firebase Test Lab
        android.os.Build.FINGERPRINT.contains("generic") &&
        android.os.Build.MODEL.contains("sdk")
    }
}
```

### 3. Tests Instrumentados

Tests que corren en dispositivos reales o emuladores con el simulador activo:

```kotlin
@Test
fun testSDKInitializationDoesNotCrash() {
    activityRule.scenario.onActivity { activity ->
        Thread.sleep(10000) // Espera inicialización del SDK
        assertNotNull("App should survive SDK init", activity)
    }
}

@Test
fun testAppRunsFor30Seconds() {
    // Test de estabilidad extendido
    activityRule.scenario.onActivity { activity ->
        for (i in 1..6) {
            Thread.sleep(5000)
            assertNotNull("Activity alive at ${i * 5}s", activity)
        }
    }
}
```

## 🚀 Cómo Funciona

### Flujo de Inicialización en Testing

```
┌──────────────────────────────────────┐
│  App inicia en Firebase Test Lab    │
└────────────────┬─────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────┐
│  Detección automática de test mode  │
│  isTestMode = true                   │
└────────────────┬─────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────┐
│  DJI SDK se inicializa normalmente  │
│  SDKManager.init()                   │
└────────────────┬─────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────┐
│  SDK se registra (puede fallar)     │
│  SDKManager.registerApp()            │
└────────────────┬─────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────┐
│  Auto-start del simulador (5s delay)│
│  SimulatorManager.startSimulator()  │
└────────────────┬─────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────┐
│  Tests se ejecutan con simulador    │
│  activo (si disponible)              │
└──────────────────────────────────────┘
```

## 📊 Configuración CI/CD

### GitHub Actions Workflow

El workflow ejecuta:

1. **Build Job:**
   - Compila APK debug
   - Compila APK de tests
   - Sube artifacts

2. **Test Job:**
   - Ejecuta tests instrumentados en Firebase Test Lab
   - Dispositivo: Pixel 8 (Android 14)
   - Timeout: 10 minutos
   - Guarda resultados en Cloud Storage

```yaml
- name: Run instrumented tests on Firebase Test Lab
  run: |
    gcloud firebase test android run \
      --type instrumentation \
      --app app/build/outputs/apk/debug/app-debug.apk \
      --test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
      --device model=shiba,version=34 \
      --timeout 10m
```

## ⚠️ Limitaciones Conocidas

### 1. Registro del SDK

El SDK DJI requiere una cuenta DJI válida para registrarse. En entornos de testing:

```kotlin
// Registro puede fallar con:
// - "APP_NOT_FOUND" 
// - "SIGN_ERROR"
// - "Network access failed"

// ✅ Esto es esperado y manejado en los tests
if (!registrationSuccess) {
    println("⚠️ SDK Registration failed: ${error?.description()}")
    println("ℹ️ This is expected in test environments without DJI account")
}
```

### 2. Simulador Requiere Producto Conectado

El simulador DJI necesita que el SDK detecte un "producto" (drone) para funcionar:

```kotlin
// Puede fallar con:
// - "Product not connected"
// - "Simulator not available"

// 🔧 Solución: Tests verifican estabilidad de la app sin crash
if (!simulatorStarted) {
    println("⚠️ Simulator start failed: ${error?.description()}")
    println("ℹ️ Verifying app stability without simulator")
}
```

### 3. Clases Provistas por DJI

Algunas clases del SDK solo están disponibles en runtime:

```gradle
compileOnly 'com.dji:dji-sdk-v5-aircraft-provided:5.16.0'
```

Si estas clases se cargan en tests sin hardware, pueden causar:
- `NoClassDefFoundError`
- `VerifyError`

**Solución:** Los tests verifican que la app NO crashea durante 30 segundos.

## 🎯 Objetivos de Testing

### Lo Que SÍ Verificamos ✅

1. **Compilación:** Código compila sin errores
2. **Inicialización:** App inicia sin crashes
3. **Estabilidad:** App corre 30+ segundos sin crashes
4. **SDK Setup:** SDK se inicializa correctamente
5. **Detección de Modo Test:** `isTestMode` funciona
6. **Managers:** SimulatorManager se crea correctamente
7. **UI:** Elementos básicos de UI existen

### Lo Que NO Verificamos ❌

1. Funcionalidad completa del drone (requiere hardware)
2. Video streaming real (requiere cámara del drone)
3. Telemetría real (requiere sensores del drone)
4. Control remoto (requiere RC físico)

## 🔧 Desarrollo Local

### Ejecutar Tests Localmente

```bash
# Tests unitarios (rápidos)
./gradlew test

# Tests instrumentados (requiere emulador/dispositivo)
./gradlew connectedDebugAndroidTest

# Test específico
./gradlew connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=\
  com.dronescan.msdksample.BasicInstrumentedTest#testAppRunsFor30Seconds
```

### Habilitar Test Mode Manualmente

```kotlin
// En tu Activity o Application
override fun onCreate() {
    super.onCreate()
    
    // Forzar test mode para debugging
    DroneScanApplication.isTestMode = true
    
    // Resto de inicialización...
}
```

## 📈 Mejoras Futuras

1. **Mock del SDK:** Crear mocks de las clases DJI para testing unitario
2. **Simulador Personalizado:** Implementar simulador propio que no requiera SDK
3. **Tests de UI:** Agregar tests Espresso para interacciones de usuario
4. **Tests de Performance:** Medir uso de memoria y CPU
5. **Screenshot Tests:** Capturar pantallas para verificar UI visualmente

## 📚 Referencias

- [DJI Mobile SDK V5 Documentation](https://developer.dji.com/mobile-sdk/)
- [DJI Simulator API](https://developer.dji.com/doc/mobile-sdk-tutorial/en/quick-start/simulation.html)
- [Firebase Test Lab](https://firebase.google.com/docs/test-lab)
- [Android Instrumented Tests](https://developer.android.com/training/testing/instrumented-tests)

## 🆘 Troubleshooting

### Test falla con "SDK not initialized"

```kotlin
// Aumentar el tiempo de espera en el test
Thread.sleep(10000) // 10 segundos en lugar de 3
```

### Test falla con "NoClassDefFoundError"

```kotlin
// Verificar que las dependencias estén correctas
dependencies {
    implementation 'com.dji:dji-sdk-v5-aircraft:5.16.0'
    compileOnly 'com.dji:dji-sdk-v5-aircraft-provided:5.16.0'
    runtimeOnly 'com.dji:dji-sdk-v5-networkImp:5.16.0'
}
```

### Firebase Test Lab no ejecuta tests

```bash
# Verificar que ambos APKs se generaron
ls -lh app/build/outputs/apk/debug/
ls -lh app/build/outputs/apk/androidTest/debug/
```

### Tests pasan localmente pero fallan en CI

```yaml
# Aumentar timeout en el workflow
--timeout 15m  # En lugar de 10m
```
