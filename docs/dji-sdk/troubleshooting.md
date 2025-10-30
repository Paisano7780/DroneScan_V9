# Problemas Comunes y Soluciones - DJI SDK V5

## 🔴 Error: Smart Cast en Kotlin

### Síntoma
```
Smart cast to 'SimulatorStatusListener' is impossible, because 'simulatorStatusListener' 
is a mutable property that could have been changed by this time
```

### Causa
Kotlin no puede hacer smart cast de propiedades mutables (`var`) porque podrían cambiar entre la verificación null y el uso.

### Solución
Usar una variable local inmutable:

```kotlin
// ❌ Incorrecto
if (simulatorStatusListener == null) {
    simulatorStatusListener = SimulatorStatusListener { state -> ... }
    DJISimulatorManager.getInstance().addSimulatorStateListener(simulatorStatusListener)
    //                                                            ^^^^^^^^^^^^^^^^^^^^^^
    //                                                            Error aquí
}

// ✅ Correcto
if (simulatorStatusListener == null) {
    val listener = SimulatorStatusListener { state -> ... }
    simulatorStatusListener = listener
    DJISimulatorManager.getInstance().addSimulatorStateListener(listener)
}
```

---

## 🔴 Error: Unresolved reference 'start' / 'stop'

### Síntoma
```
Unresolved reference: start
Unresolved reference: stop
```

### Causa
Los métodos `start()` y `stop()` son de SDK V4. En V5 fueron renombrados.

### Solución
Usar los nombres correctos de V5:

```kotlin
// ❌ V4 (Obsoleto)
DJISimulatorManager.getInstance().start()
DJISimulatorManager.getInstance().stop()

// ✅ V5 (Correcto)
DJISimulatorManager.getInstance().enableSimulator(settings, callback)
DJISimulatorManager.getInstance().disableSimulator(callback)
```

---

## 🔴 Error: Function invocation 'areMotorsOn()' expected

### Síntoma
```
Function invocation 'areMotorsOn()' expected
```

### Causa
`areMotorsOn` es un método en V5, no una propiedad.

### Solución
Agregar paréntesis:

```kotlin
// ❌ Incorrecto
if (state.areMotorsOn) { ... }

// ✅ Correcto
if (state.areMotorsOn()) { ... }
```

---

## 🔴 Error: Unresolved reference 'altitude'

### Síntoma
```
Unresolved reference: altitude
```

### Causa
La propiedad `altitude` fue renombrada a `positionZ` en V5.

### Solución
```kotlin
// ❌ V4 (Obsoleto)
val alt = state.altitude

// ✅ V5 (Correcto)
val alt = state.positionZ
```

---

## 🔴 Error: Too many arguments for createInstance

### Síntoma
```
Too many arguments for public fun createInstance(...)
```

### Causa
En V5, `InitializationSettings.createInstance()` solo acepta 2 parámetros.

### Solución
```kotlin
// ❌ V4 (3 parámetros)
val settings = InitializationSettings.createInstance(
    location,
    satelliteCount,
    updateFrequency  // Este parámetro ya no existe
)

// ✅ V5 (2 parámetros)
val settings = InitializationSettings.createInstance(
    location,
    satelliteCount
)
```

---

## 🔴 Error: Cannot access class 'IDJIError' en androidTest

### Síntoma
```
Cannot access class 'dji.v5.common.error.IDJIError'. Check your module classpath 
for missing or conflicting dependencies
```

### Causa
Las dependencias DJI no están correctamente configuradas para tests instrumentados.

### Solución
Agregar en `app/build.gradle`:

```gradle
dependencies {
    // Runtime dependencies
    implementation 'com.dji:dji-sdk-v5-aircraft:5.16.0'
    compileOnly 'com.dji:dji-sdk-v5-aircraft-provided:5.16.0'
    runtimeOnly 'com.dji:dji-sdk-v5-networkImp:5.16.0'
    
    // ⚠️ IMPORTANTE: Agregar para androidTest
    androidTestImplementation 'com.dji:dji-sdk-v5-aircraft:5.16.0'
    androidTestCompileOnly 'com.dji:dji-sdk-v5-aircraft-provided:5.16.0'
}
```

---

## 🟡 Warning: Parameter never used

### Síntoma
```
Parameter 'updateFrequency' is never used
```

### Causa
El parámetro existe en la firma del método pero no se usa internamente.

### Solución (Opcional)
Suprimir el warning o marcar con `@Suppress`:

```kotlin
fun startSimulator(
    latitude: Double = DEFAULT_LATITUDE,
    longitude: Double = DEFAULT_LONGITUDE,
    satelliteCount: Int = DEFAULT_SATELLITE_COUNT,
    @Suppress("UNUSED_PARAMETER")
    updateFrequency: Int = DEFAULT_UPDATE_FREQUENCY,  // Ignorado en V5
    callback: CommonCallbacks.CompletionCallback? = null
) {
    // updateFrequency ya no se usa en createInstance de V5
    val settings = InitializationSettings.createInstance(
        LocationCoordinate2D(latitude, longitude),
        satelliteCount
    )
    // ...
}
```

---

## 🔴 Error: SDK Registration Failed

### Síntoma
```
Register Failure: App key is invalid
```

### Causa
La API Key de DJI no está registrada o es inválida.

### Solución
1. Obtener API Key en [DJI Developer Portal](https://developer.dji.com/user/apps)
2. Agregar en `AndroidManifest.xml`:

```xml
<application>
    <meta-data
        android:name="com.dji.sdk.API_KEY"
        android:value="TU_API_KEY_AQUI" />
</application>
```

3. Inicializar SDK en `Application.onCreate()`:

```kotlin
class DroneScanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SDKManager.getInstance().init(this, object : SDKManagerCallback {
            override fun onRegisterSuccess() {
                Log.d(TAG, "SDK registered successfully")
            }
            
            override fun onRegisterFailure(error: IDJIError) {
                Log.e(TAG, "SDK registration failed: ${error.description()}")
            }
            // ...
        })
    }
}
```

---

## 🔴 Error: Product Connection Timeout

### Síntoma
El simulador no se inicia o no hay conexión con el producto.

### Solución
1. **Verificar registro SDK**: Debe estar registrado antes de usar cualquier API
2. **Usar simulador**: Para desarrollo sin hardware físico
3. **Esperar callback**: No llamar APIs hasta que `onProductConnected` se dispare

```kotlin
// Esperar conexión del producto
ProductConnectionManager.getInstance().addProductConnectionListener { connected ->
    if (connected) {
        Log.d(TAG, "Product connected, can now use simulator")
        startSimulator()
    }
}
```

---

## 📚 Recursos Adicionales

- [GitHub Issues - Mobile-SDK-Android-V5](https://github.com/dji-sdk/Mobile-SDK-Android-V5/issues)
- [DJI Forum](https://forum.dji.com/forum-139-1.html)
- [Stack Overflow - DJI Tag](https://stackoverflow.com/questions/tagged/dji)

---

## ✅ Checklist de Depuración

Antes de reportar un issue, verificar:

- [ ] ¿Estás usando la versión correcta del SDK? (V5.16.0)
- [ ] ¿Las dependencias están correctamente configuradas en `build.gradle`?
- [ ] ¿El SDK está registrado exitosamente?
- [ ] ¿Estás usando los nombres de métodos de V5 (no V4)?
- [ ] ¿Los parámetros de `createInstance()` son correctos? (solo 2)
- [ ] ¿`areMotorsOn()` se llama con paréntesis?
- [ ] ¿Usas `positionZ` en lugar de `altitude`?
- [ ] ¿Las variables mutables tienen smart cast correcto?

---

**Última actualización**: 2025-10-30
