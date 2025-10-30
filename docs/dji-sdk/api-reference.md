# DJI Mobile SDK V5 - API Reference

## SimulatorManager API

### Package
`dji.v5.manager.aircraft.simulator.SimulatorManager`

### Métodos Principales

#### getInstance()
```kotlin
SimulatorManager.getInstance(): SimulatorManager
```
Obtiene la instancia singleton del SimulatorManager.

#### enableSimulator()
```kotlin
fun enableSimulator(
    settings: InitializationSettings,
    callback: CommonCallbacks.CompletionCallback?
)
```
Activa el simulador con la configuración especificada.

**Parámetros**:
- `settings`: Configuración inicial (ubicación, satélites)
- `callback`: Callback de completación (opcional)

**Ejemplo**:
```kotlin
val location = LocationCoordinate2D(22.5431, 114.0579)
val settings = InitializationSettings.createInstance(location, 12)

SimulatorManager.getInstance().enableSimulator(settings) { error ->
    if (error == null) {
        Log.d(TAG, "Simulator started successfully")
    } else {
        Log.e(TAG, "Failed to start simulator: ${error.description()}")
    }
}
```

#### disableSimulator()
```kotlin
fun disableSimulator(
    callback: CommonCallbacks.CompletionCallback?
)
```
Desactiva el simulador.

#### addSimulatorStateListener()
```kotlin
fun addSimulatorStateListener(
    listener: SimulatorStatusListener
)
```
Agrega un listener para cambios de estado del simulador.

**Nota importante**: En Kotlin, para evitar errores de smart cast, usar variable local:
```kotlin
val listener = SimulatorStatusListener { state ->
    // Handle state updates
}
simulatorStatusListener = listener
SimulatorManager.getInstance().addSimulatorStateListener(listener)
```

#### removeSimulatorStateListener()
```kotlin
fun removeSimulatorStateListener(
    listener: SimulatorStatusListener
)
```
Remueve un listener de estado del simulador.

#### isSimulatorEnabled
```kotlin
val isSimulatorEnabled: Boolean
```
Propiedad que indica si el simulador está activo.

---

## InitializationSettings

### Package
`dji.v5.manager.aircraft.simulator.InitializationSettings`

### Métodos

#### createInstance()
```kotlin
static fun createInstance(
    location: LocationCoordinate2D,
    satelliteCount: Int  // Rango: [0, 20]
): InitializationSettings
```

**Parámetros**:
- `location`: Coordenadas iniciales (lat/lon)
- `satelliteCount`: Número de satélites GPS (0-20)

**Nota**: En V5, solo acepta **2 parámetros**. El parámetro `updateFrequency` fue removido.

---

## SimulatorState

### Package
`dji.v5.manager.aircraft.simulator.SimulatorState`

### Propiedades

| Propiedad | Tipo | Descripción |
|-----------|------|-------------|
| `location` | `LocationCoordinate2D` | Ubicación actual |
| `positionX` | `Double` | Posición X en metros |
| `positionY` | `Double` | Posición Y en metros |
| `positionZ` | `Double` | Altitud en metros |
| `roll` | `Float` | Ángulo de roll |
| `pitch` | `Float` | Ángulo de pitch |
| `yaw` | `Float` | Ángulo de yaw |
| `isFlying` | `Boolean` | Si está volando |

### Métodos

#### areMotorsOn()
```kotlin
fun areMotorsOn(): Boolean
```
⚠️ **Es un método, NO una propiedad**. Debe llamarse con paréntesis.

```kotlin
// ❌ Incorrecto
if (state.areMotorsOn) { ... }

// ✅ Correcto  
if (state.areMotorsOn()) { ... }
```

---

## SimulatorStatusListener

### Package
`dji.v5.manager.aircraft.simulator.SimulatorStatusListener`

### Interface

```kotlin
interface SimulatorStatusListener {
    fun onUpdate(state: SimulatorState)
}
```

**Uso con lambda**:
```kotlin
val listener = SimulatorStatusListener { state ->
    Log.d(TAG, "Lat: ${state.location.latitude}")
    Log.d(TAG, "Lon: ${state.location.longitude}")
    Log.d(TAG, "Alt: ${state.positionZ}")
    Log.d(TAG, "Flying: ${state.areMotorsOn()}")
}
```

---

## CommonCallbacks

### Package
`dji.v5.common.callback.CommonCallbacks`

### CompletionCallback

```kotlin
interface CompletionCallback {
    fun onSuccess()
    fun onFailure(error: IDJIError)
}
```

**Uso**:
```kotlin
object : CommonCallbacks.CompletionCallback {
    override fun onSuccess() {
        // Success handling
    }
    
    override fun onFailure(error: IDJIError) {
        Log.e(TAG, "Error: ${error.description()}")
    }
}
```

---

## Cambios Importantes de V4 a V5

| V4 (Obsoleto) | V5 (Actual) | Notas |
|---------------|-------------|-------|
| `start()` | `enableSimulator()` | Requiere InitializationSettings |
| `stop()` | `disableSimulator()` | - |
| `addSimulatorStatusListener()` | `addSimulatorStateListener()` | Nombre actualizado |
| `removeSimulatorStatusListener()` | `removeSimulatorStateListener()` | Nombre actualizado |
| `state.altitude` | `state.positionZ` | Propiedad renombrada |
| `state.areMotorsOn` | `state.areMotorsOn()` | Ahora es método |
| `createInstance(loc, sat, freq)` | `createInstance(loc, sat)` | Solo 2 parámetros |

---

## Referencias
- [Official API Docs](https://github.com/dji-sdk/Mobile-SDK-Android-V5/tree/main/Docs/Android_API/en/Components/ISimulatorManager)
- [Sample Code](https://github.com/dji-sdk/Mobile-SDK-Android-V5/tree/main/SampleCode-V5)
