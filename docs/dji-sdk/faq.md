# FAQ - Preguntas Frecuentes DJI SDK V5

## 🚀 Inicio y Configuración

### ¿Cómo obtengo una App Key?

1. Regístrate en [DJI Developer](https://developer.dji.com/)
2. Ve a "User Center" → "Apps"
3. Crea nueva aplicación con tu package name
4. Copia la App Key generada
5. Agrégala en `AndroidManifest.xml`:
```xml
<meta-data
    android:name="com.dji.sdk.API_KEY"
    android:value="TU_APP_KEY_AQUI" />
```

### ¿Puedo usar el SDK sin drone físico?

✅ **Sí, con el Simulador:**

```kotlin
val location = LocationCoordinate2D(22.5431, 114.0579)
val settings = InitializationSettings.createInstance(location, 12)

SimulatorManager.getInstance().enableSimulator(settings) { error ->
    if (error == null) {
        Log.d(TAG, "✅ Simulador activado")
    }
}
```

El simulador permite:
- Probar comandos de vuelo
- Ver telemetría simulada
- Desarrollar sin hardware
- Testing automatizado

**Limitaciones**:
- No simula cámara real
- No simula todas las condiciones ambientales
- Algunos sensores no están disponibles

### ¿Cuál es la diferencia entre V4 y V5 del SDK?

| Característica | V4 | V5 |
|---------------|----|----|
| Arquitectura | Basado en callbacks | Basado en Key-Value |
| Lenguaje | Java | Kotlin-first |
| UI | UXSDK separado | Integrado |
| Threading | Manual | Coroutines |
| Productos | Hasta 2020 | 2020+ (Mavic 3, M30, etc.) |
| API | `DJISDKManager` | `SDKManager` |
| Listeners | Múltiples callbacks | Observables centralizados |

**Migración V4 → V5**:
- Muchos nombres de métodos cambiaron
- `setStateCallback` → `addStateListener`
- `startSimulator` → `enableSimulator`
- Los callbacks ahora son más consistentes
- Ver [api-reference.md](./api-reference.md) para mapeo completo

### ¿Qué versión de Android necesito?

- **MinSDK**: 24 (Android 7.0)
- **TargetSDK**: 34 (Android 14) recomendado
- **CompileSDK**: 34

```gradle
android {
    compileSdk 34
    defaultConfig {
        minSdk 24
        targetSdk 34
    }
}
```

## 📱 Problemas de Compilación

### Error: "App key is invalid"

**Causas comunes**:
1. App Key incorrecta
2. Package name no coincide
3. App Key de entorno equivocado (dev vs prod)

**Solución**:
```xml
<!-- Verificar en AndroidManifest.xml -->
<meta-data
    android:name="com.dji.sdk.API_KEY"
    android:value="TU_APP_KEY_REAL" />
```
- Verificar en DJI Developer que el package name coincida EXACTAMENTE
- No debe haber espacios ni caracteres extra

### Error: "Network library not found"

**Causa**: Falta dependencia de red en runtime

**Solución**:
```gradle
dependencies {
    runtimeOnly 'com.dji:dji-sdk-v5-networkImp:5.16.0'
}
```

⚠️ **Importante**: Es `runtimeOnly`, NO `implementation`

### Error: "Multiple dex files define..."

**Causa**: Conflictos en librerías nativas (.so)

**Solución**:
```gradle
android {
    packagingOptions {
        pickFirst 'lib/*/libdjivideo.so'
        pickFirst 'lib/*/libSDKRelativeJNI.so'
        pickFirst 'lib/*/libFlyForbid.so'
        pickFirst 'lib/*/libduml_vision_bokeh.so'
        pickFirst 'lib/*/libyuv2.so'
        pickFirst 'lib/*/libc++_shared.so'
    }
}
```

### Error: "Unresolved reference: common" en tests

**Causa**: DJI SDK no disponible en scope androidTest

**Solución**:
```gradle
dependencies {
    // Para tests instrumentados
    androidTestImplementation 'com.dji:dji-sdk-v5-aircraft:5.16.0'
    androidTestCompileOnly 'com.dji:dji-sdk-v5-aircraft-provided:5.16.0'
}
```

### Error: "Smart cast to 'X' is impossible"

**Causa**: Kotlin no puede garantizar nullability en lambdas

**Solución**:
```kotlin
// ❌ Mal
SimulatorManager.getInstance().addSimulatorStateListener { state ->
    Log.d(TAG, "Lat: ${state.location.latitude}") // Error
}

// ✅ Bien
SimulatorManager.getInstance().addSimulatorStateListener { state ->
    val location = state?.location ?: return@addSimulatorStateListener
    Log.d(TAG, "Lat: ${location.latitude}")
}
```

## 🔌 Conexión y Hardware

### ¿Cómo conecto mi drone?

**Opción 1: USB (recomendado para desarrollo)**
1. Conecta RC al móvil vía USB
2. Conecta drone al RC
3. La app detectará automáticamente

**Opción 2: WiFi**
1. Enciende drone y RC
2. Conecta móvil a WiFi del drone
3. La app detectará la conexión

**Listeners de conexión**:
```kotlin
SDKManager.getInstance().init(context, object : SDKManagerCallback {
    override fun onProductConnect(productId: Int) {
        Log.d(TAG, "🔌 Producto conectado: $productId")
    }

    override fun onProductDisconnect(productId: Int) {
        Log.d(TAG, "🔌 Producto desconectado")
    }
})
```

### ¿Por qué no detecta mi drone?

**Checklist de diagnóstico**:

1. **Verificar permisos**:
```xml
<uses-permission android:name="android.permission.USB_PERMISSION" />
<uses-permission android:name="android.permission.INTERNET" />
```

2. **Verificar USB filter**:
```xml
<!-- res/xml/accessory_filter.xml -->
<usb-accessory
    android:manufacturer="DJI"
    android:model=".*" />
```

3. **Verificar intent filter**:
```xml
<intent-filter>
    <action android:name="android.hardware.usb.action.USB_ACCESSORY_ATTACHED" />
</intent-filter>
```

4. **Verificar SDK inicializado**:
```kotlin
val isRegistered = SDKManager.getInstance().isRegistered
Log.d(TAG, "SDK Registered: $isRegistered")
```

5. **Verificar logs**:
```bash
adb logcat | grep -i "dji"
```

### ¿Funciona con control remoto de terceros?

No. El SDK requiere:
- Control remoto oficial DJI
- O conexión WiFi directa (solo modelos compatibles)

## 🎮 Control de Vuelo

### ¿Cómo despego el drone?

```kotlin
FlightControllerManager.getInstance().startTakeoff(object : CommonCallbacks.CompletionCallback {
    override fun onSuccess() {
        Log.d(TAG, "✈️ Despegue exitoso")
    }

    override fun onFailure(error: IDJIError) {
        Log.e(TAG, "❌ Error: ${error.description()}")
    }
})
```

**Pre-requisitos**:
- Motores encendidos
- GPS suficiente (>= 4 satélites)
- No hay errores críticos
- Home point establecido

### ¿Cómo controlo manualmente el drone?

**Virtual Stick Mode**:
```kotlin
// Activar modo virtual stick
FlightControllerManager.getInstance().enableVirtualStickControlMode(object : CommonCallbacks.CompletionCallback {
    override fun onSuccess() {
        // Enviar comandos de control
        val command = VirtualStickFlightControlParam(
            pitch = 1.0f,  // Forward
            roll = 0.0f,   // Sin lateral
            yaw = 0.0f,    // Sin rotación
            verticalThrottle = 0.0f  // Altura constante
        )
        
        FlightControllerManager.getInstance().sendVirtualStickAdvancedParam(command)
    }

    override fun onFailure(error: IDJIError) {
        Log.e(TAG, "Error: ${error.description()}")
    }
})
```

**Nota**: Requiere enviar comandos cada 200ms o se desactiva automáticamente.

### ¿Puedo hacer waypoints (misiones automáticas)?

✅ Sí, con **WaypointMissionManager**:

```kotlin
val mission = WaypointMission.Builder()
    .setMaxFlightSpeed(15f)
    .setAutoFlightSpeed(10f)
    .setFinishedAction(WaypointMissionFinishedAction.GO_HOME)
    .setGimbalPitchRotationEnabled(true)
    .setHeadingMode(WaypointMissionHeadingMode.AUTO)
    .build()

// Agregar waypoints
mission.addWaypoint(
    Waypoint(22.5431, 114.0579, 30f) // lat, lon, altitude
)

WaypointMissionManager.getInstance().loadMission(mission) { error ->
    if (error == null) {
        WaypointMissionManager.getInstance().startMission { startError ->
            // Misión iniciada
        }
    }
}
```

## 📸 Cámara

### ¿Cómo tomo una foto?

```kotlin
CameraManager.getInstance().startShootPhoto(object : CommonCallbacks.CompletionCallback {
    override fun onSuccess() {
        Log.d(TAG, "📸 Foto tomada")
    }

    override fun onFailure(error: IDJIError) {
        Log.e(TAG, "Error: ${error.description()}")
    }
})
```

### ¿Cómo inicio grabación de video?

```kotlin
CameraManager.getInstance().startRecordVideo(object : CommonCallbacks.CompletionCallback {
    override fun onSuccess() {
        Log.d(TAG, "🎥 Grabación iniciada")
    }

    override fun onFailure(error: IDJIError) {
        Log.e(TAG, "Error: ${error.description()}")
    }
})
```

### ¿Cómo cambio configuración de cámara?

**Sistema Key-Value en V5**:
```kotlin
val key = CameraKey.createCameraKey(CameraLensType.CAMERA_LENS_DEFAULT)
    .createChildKey(CameraKey.ISO)

KeyManager.getInstance().setValue(key, CameraISO.ISO_100, object : SetCallback {
    override fun onSuccess() {
        Log.d(TAG, "ISO cambiado a 100")
    }

    override fun onFailure(error: IDJIError) {
        Log.e(TAG, "Error: ${error.description()}")
    }
})
```

### ¿Cómo obtengo el stream de video?

**Con FPVWidget (recomendado)**:
```xml
<dji.v5.ux.core.widget.fpv.FPVWidget
    android:id="@+id/fpvWidget"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

**Manualmente**:
```kotlin
VideoChannelManager.getInstance().getVideoChannel(0).addVideoFrameListener { data, width, height ->
    // data: ByteArray con frame YUV
    // Procesar/decodificar frame
}
```

## 🗺️ GPS y Navegación

### ¿Cómo obtengo la ubicación del drone?

```kotlin
val key = FlightControllerKey.createFlightControllerKey(FlightControllerKey.LOCATION)

KeyManager.getInstance().getValue(key, object : GetCallback {
    override fun onSuccess(value: Any?) {
        val location = value as? LocationCoordinate3D
        Log.d(TAG, "Ubicación: ${location?.latitude}, ${location?.longitude}, ${location?.altitude}")
    }

    override fun onFailure(error: IDJIError) {
        Log.e(TAG, "Error: ${error.description()}")
    }
})
```

### ¿Cómo activo RTK?

```kotlin
RTKManager.getInstance().enableRTK(object : CommonCallbacks.CompletionCallback {
    override fun onSuccess() {
        Log.d(TAG, "RTK activado")
    }

    override fun onFailure(error: IDJIError) {
        Log.e(TAG, "Error: ${error.description()}")
    }
})
```

## 🔋 Batería

### ¿Cómo monitoreo la batería?

```kotlin
val key = BatteryKey.createBatteryKey(0).createChildKey(BatteryKey.CHARGE_REMAINING_IN_PERCENT)

KeyManager.getInstance().listen(key, this, object : KeyListener<Int> {
    override fun onValueChange(oldValue: Int?, newValue: Int?) {
        Log.d(TAG, "Batería: $newValue%")
        
        if (newValue != null && newValue < 20) {
            Log.w(TAG, "⚠️ Batería baja!")
        }
    }
})
```

## 🧪 Testing

### ¿Cómo escribo tests para DJI SDK?

**Test Instrumentado**:
```kotlin
@RunWith(AndroidJUnit4::class)
class DroneTest {

    @Test
    fun testSimulatorStart() {
        val location = LocationCoordinate2D(22.5431, 114.0579)
        val settings = InitializationSettings.createInstance(location, 12)
        
        var success = false
        val latch = CountDownLatch(1)
        
        SimulatorManager.getInstance().enableSimulator(settings) { error ->
            success = (error == null)
            latch.countDown()
        }
        
        latch.await(5, TimeUnit.SECONDS)
        assertTrue(success)
    }
}
```

### ¿Cómo testeo en CI/CD?

**Firebase Test Lab**:
```yaml
# .github/workflows/android-ci.yml
- name: Run instrumented tests on Firebase Test Lab
  run: |
    gcloud firebase test android run \
      --type instrumentation \
      --app app/build/outputs/apk/debug/app-debug.apk \
      --test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
      --device model=Pixel8,version=34
```

## 🐛 Debugging

### ¿Cómo veo logs del SDK?

```bash
# Todos los logs DJI
adb logcat | grep -i "dji"

# Solo errores
adb logcat *:E | grep -i "dji"

# Logs específicos
adb logcat DJI-SDK:D *:S
```

### ¿Dónde veo errores de SDK?

Los errores implementan `IDJIError`:
```kotlin
error.errorCode()      // Código numérico
error.description()    // Descripción legible
error.toString()       // String completo
```

Consulta [troubleshooting.md](./troubleshooting.md) para errores comunes.

## 🚀 Performance

### ¿Cómo optimizo el rendimiento?

1. **Video decoding**: Usa hardware decode
```kotlin
fpvWidget.setVideoDecodeType(VideoDecodeType.HARDWARE)
```

2. **Threading**: Usa coroutines para operaciones largas
```kotlin
lifecycleScope.launch(Dispatchers.IO) {
    // Operación pesada
}
```

3. **Listeners**: Limpia listeners no usados
```kotlin
override fun onDestroy() {
    SimulatorManager.getInstance().clearAllSimulatorStateListener()
    super.onDestroy()
}
```

4. **Memory**: No guardes referencias a objetos grandes

## 📦 Productos Soportados

### ¿Qué drones son compatibles con V5?

**Mavic Series**:
- Mavic 3 (todas las variantes)
- Mavic 3 Enterprise
- Mavic 2 Enterprise Advanced

**Matrice Series**:
- M30/M30T
- M300 RTK (firmware 2021+)
- M350 RTK

**Mini/Air Series**:
- Mini 3 Pro
- Air 2S (limitado)

**Phantom/Inspire** (modelos antiguos): Solo V4 SDK

Ver lista completa en [sdk-architecture.md](./sdk-architecture.md#productos-soportados)

## 🔐 Seguridad

### ¿Cómo manejo permisos en runtime?

```kotlin
// Android 6.0+
if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    != PackageManager.PERMISSION_GRANTED) {
    
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
        LOCATION_PERMISSION_REQUEST
    )
}
```

### ¿Puedo usar el SDK en producción sin restricciones?

✅ Sí, pero:
- Cumple regulaciones locales de drones
- No uses para actividades ilegales
- Respeta zonas no-fly
- Obtén permisos necesarios

DJI implementa restricciones de GeoZone automáticamente.

## 📚 Recursos Adicionales

- [API Reference](./api-reference.md)
- [Troubleshooting](./troubleshooting.md)
- [SDK Architecture](./sdk-architecture.md)
- [Installation Guide](./installation-guide.md)
- [UI Components](./ui-components.md)

## 💡 Tips Generales

1. **Siempre verifica errores**: Nunca asumas que un callback es exitoso
2. **Usa el simulador**: Desarrolla sin drone, prueba en hardware al final
3. **Lee la documentación oficial**: Este FAQ es un complemento, no un reemplazo
4. **Chequea versiones**: SDK, firmware de drone, y RC deben ser compatibles
5. **Únete a la comunidad**: [DJI Developer Forum](https://forum.dji.com/forum-139-1.html)

---

**Última actualización**: 2025-10-30

