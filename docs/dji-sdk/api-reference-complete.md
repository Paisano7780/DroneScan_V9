# DJI Mobile SDK V5 - API Reference Completa

Documentación completa basada en la API Reference oficial de DJI SDK V5 para Android.

**Fuente**: [DJI API Reference V5](https://developer.dji.com/api-reference-v5/android-api/index.html)

## 📦 Estructura de Paquetes

### Core Packages

#### `dji.v5.manager`
Managers principales para control del drone y componentes.

**Clases Principales**:
- `SDKManager` - Gestor principal del SDK, inicialización y registro
- `KeyManager` - Sistema Key-Value para lectura/escritura de propiedades
- `AircraftManager` - Gestión de aeronaves conectadas
- `RCManager` - Gestión de control remoto

#### `dji.v5.manager.aircraft`
Managers específicos de aeronaves.

**Sub-packages**:
- `simulator/` - Simulador de vuelo
- `flightcontrol/` - Control de vuelo
- `camera/` - Control de cámara
- `gimbal/` - Control de gimbal
- `battery/` - Gestión de batería
- `rtk/` - RTK positioning
- `perception/` - Sensores de percepción
- `payload/` - Gestión de payloads

#### `dji.v5.common`
Clases comunes y utilidades.

**Sub-packages**:
- `callback/` - Interfaces de callback
- `error/` - Manejo de errores
- `utils/` - Utilidades
- `register/` - Registro de productos

## 🎮 SDKManager

### Inicialización

```kotlin
class SDKManager private constructor() {
    companion object {
        fun getInstance(): SDKManager
    }
    
    fun init(
        context: Context,
        callback: SDKManagerCallback
    )
    
    fun registerApp(callback: CommonCallbacks.CompletionCallbackWithParam<Boolean>)
    
    fun destroy()
}
```

### SDKManagerCallback

```kotlin
interface SDKManagerCallback {
    // Registro exitoso
    fun onRegisterSuccess()
    
    // Fallo en registro
    fun onRegisterFailure(error: IDJIError)
    
    // Producto conectado
    fun onProductConnect(productId: Int)
    
    // Producto desconectado
    fun onProductDisconnect(productId: Int)
    
    // Producto cambiado
    fun onProductChanged(productId: Int)
    
    // Proceso de inicialización
    fun onInitProcess(event: SDKInitEvent, totalProcess: Int)
    
    // Progreso de descarga de base de datos
    fun onDatabaseDownloadProgress(current: Long, total: Long)
}
```

### SDKInitEvent

```kotlin
enum class SDKInitEvent {
    INITIALIZE_STARTED,
    INITIALIZE_FAILED,
    INITIALIZE_COMPLETE,
    DATABASE_DOWNLAND_STARTED,
    DATABASE_DOWNLAND_PROGRESS,
    DATABASE_DOWNLAND_FINISHED,
    DATABASE_IMPORT_STARTED,
    DATABASE_IMPORT_FINISHED
}
```

## 🔑 KeyManager

Sistema Key-Value para acceso a propiedades del drone.

### Conceptos Básicos

```kotlin
class KeyManager private constructor() {
    companion object {
        fun getInstance(): KeyManager
    }
    
    // Obtener valor
    fun <T> getValue(
        key: DJIKey<T>,
        callback: GetCallback
    )
    
    // Establecer valor
    fun <T> setValue(
        key: DJIKey<T>,
        value: T,
        callback: SetCallback
    )
    
    // Escuchar cambios
    fun <T> listen(
        key: DJIKey<T>,
        owner: Any,
        listener: KeyListener<T>
    )
    
    // Realizar acción
    fun performAction(
        key: DJIKey<EmptyMsg>,
        callback: ActionCallback
    )
}
```

### Key Hierarchy

```kotlin
// Crear key de componente
val batteryKey = BatteryKey.create()

// Crear key específica
val chargeKey = batteryKey.createChildKey(BatteryKey.CHARGE_REMAINING_IN_PERCENT)

// Con índice (para múltiples componentes)
val battery0Key = BatteryKey.create(0)
val battery1Key = BatteryKey.create(1)
```

### Callbacks

```kotlin
// Get Callback
interface GetCallback {
    fun onSuccess(value: Any?)
    fun onFailure(error: IDJIError)
}

// Set Callback
interface SetCallback {
    fun onSuccess()
    fun onFailure(error: IDJIError)
}

// Key Listener
interface KeyListener<T> {
    fun onValueChange(oldValue: T?, newValue: T?)
}

// Action Callback
interface ActionCallback {
    fun onSuccess()
    fun onFailure(error: IDJIError)
}
```

## ✈️ SimulatorManager

Ver [api-reference.md](./api-reference.md) para documentación completa de SimulatorManager.

**Quick Reference**:

```kotlin
class SimulatorManager private constructor() {
    companion object {
        fun getInstance(): SimulatorManager
    }
    
    fun enableSimulator(
        settings: InitializationSettings,
        callback: CommonCallbacks.CompletionCallback?
    )
    
    fun disableSimulator(
        callback: CommonCallbacks.CompletionCallback?
    )
    
    fun addSimulatorStateListener(
        listener: SimulatorStateListener
    )
    
    fun removeSimulatorStateListener(
        listener: SimulatorStateListener
    )
    
    fun clearAllSimulatorStateListener()
}
```

## 🚁 FlightControllerManager

### Acciones Básicas

```kotlin
class FlightControllerManager private constructor() {
    companion object {
        fun getInstance(): FlightControllerManager
    }
    
    // Despegar
    fun startTakeoff(callback: CommonCallbacks.CompletionCallback)
    
    // Aterrizar
    fun startLanding(callback: CommonCallbacks.CompletionCallback)
    
    // Cancelar aterrizaje
    fun cancelLanding(callback: CommonCallbacks.CompletionCallback)
    
    // Return to Home
    fun startGoHome(callback: CommonCallbacks.CompletionCallback)
    
    // Cancelar RTH
    fun cancelGoHome(callback: CommonCallbacks.CompletionCallback)
    
    // Encender motores
    fun turnOnMotors(callback: CommonCallbacks.CompletionCallback)
    
    // Apagar motores
    fun turnOffMotors(callback: CommonCallbacks.CompletionCallback)
}
```

### Virtual Stick Control

```kotlin
// Habilitar modo virtual stick
fun enableVirtualStickControlMode(
    callback: CommonCallbacks.CompletionCallback
)

// Deshabilitar
fun disableVirtualStickControlMode(
    callback: CommonCallbacks.CompletionCallback
)

// Enviar comandos
fun sendVirtualStickAdvancedParam(
    param: VirtualStickFlightControlParam
)
```

### VirtualStickFlightControlParam

```kotlin
data class VirtualStickFlightControlParam(
    val pitch: Float,              // -1.0 a 1.0 (forward/backward)
    val roll: Float,               // -1.0 a 1.0 (left/right)
    val yaw: Float,                // -1.0 a 1.0 (rotation)
    val verticalThrottle: Float,   // -1.0 a 1.0 (up/down)
    val mode: VirtualStickControlMode = VirtualStickControlMode.VELOCITY
)

enum class VirtualStickControlMode {
    VELOCITY,   // Velocidad (m/s)
    POSITION,   // Posición relativa
    ANGLE       // Ángulo
}
```

### Flight Assistant

```kotlin
// Sensor de obstáculos
fun setObstacleAvoidanceEnabled(
    enabled: Boolean,
    callback: CommonCallbacks.CompletionCallback
)

// Límite de altura
fun setMaxFlightHeight(
    height: Int,  // metros
    callback: CommonCallbacks.CompletionCallback
)

// Límite de distancia
fun setMaxFlightRadius(
    radius: Int,  // metros
    callback: CommonCallbacks.CompletionCallback
)
```

## 📸 CameraManager

### Captura

```kotlin
class CameraManager private constructor() {
    companion object {
        fun getInstance(): CameraManager
    }
    
    // Tomar foto
    fun startShootPhoto(callback: CommonCallbacks.CompletionCallback)
    
    // Iniciar grabación
    fun startRecordVideo(callback: CommonCallbacks.CompletionCallback)
    
    // Detener grabación
    fun stopRecordVideo(callback: CommonCallbacks.CompletionCallback)
}
```

### Modos de Cámara

```kotlin
enum class CameraMode {
    PHOTO_NORMAL,           // Foto normal
    VIDEO_NORMAL,           // Video normal
    PHOTO_BURST,            // Ráfaga
    PHOTO_INTERVAL,         // Intervalo
    PHOTO_PANORAMA,         // Panorama
    PHOTO_HDR,              // HDR
    VIDEO_SLOW_MOTION,      // Cámara lenta
    VIDEO_HYPERLAPSE,       // Hyperlapse
    UNKNOWN
}
```

### Camera Settings Keys

```kotlin
object CameraKey {
    // Modo de cámara
    val CAMERA_MODE = "CameraMode"
    
    // ISO
    val ISO = "ISO"
    val ISO_RANGE = "ISORange"
    
    // Shutter Speed
    val SHUTTER_SPEED = "ShutterSpeed"
    val SHUTTER_SPEED_RANGE = "ShutterSpeedRange"
    
    // Aperture
    val APERTURE = "Aperture"
    val APERTURE_RANGE = "ApertureRange"
    
    // Exposure Compensation
    val EXPOSURE_COMPENSATION = "ExposureCompensation"
    
    // White Balance
    val WHITE_BALANCE = "WhiteBalance"
    
    // Zoom
    val ZOOM_RATIO = "ZoomRatio"
    val ZOOM_RATIO_RANGE = "ZoomRatioRange"
    
    // Formato de foto
    val PHOTO_FILE_FORMAT = "PhotoFileFormat"
    
    // Resolución de video
    val VIDEO_RESOLUTION_FRAME_RATE = "VideoResolutionFrameRate"
}
```

### Ejemplo: Configurar ISO

```kotlin
val key = CameraKey.create(CameraLensType.CAMERA_LENS_DEFAULT)
    .createChildKey(CameraKey.ISO)

KeyManager.getInstance().setValue(key, CameraISO.ISO_100, object : SetCallback {
    override fun onSuccess() {
        Log.d(TAG, "ISO establecido a 100")
    }
    
    override fun onFailure(error: IDJIError) {
        Log.e(TAG, "Error: ${error.description()}")
    }
})
```

## 🎥 Media Manager

### MediaFileListManager

```kotlin
class MediaFileListManager private constructor() {
    companion object {
        fun getInstance(): MediaFileListManager
    }
    
    // Obtener lista de archivos
    fun pullMediaFileListFromCamera(
        callback: CommonCallbacks.CompletionCallbackWithParam<List<MediaFile>>
    )
    
    // Descargar archivo
    fun downloadMediaFile(
        mediaFile: MediaFile,
        destDir: String,
        callback: DownloadListener<MediaFile>
    )
    
    // Eliminar archivo
    fun deleteMediaFile(
        mediaFile: MediaFile,
        callback: CommonCallbacks.CompletionCallback
    )
}
```

### MediaFile

```kotlin
data class MediaFile(
    val fileName: String,
    val fileSize: Long,
    val fileType: MediaFileType,
    val createTime: Long,
    val thumbnailUrl: String?,
    val previewUrl: String?
)

enum class MediaFileType {
    JPEG,
    DNG,
    MP4,
    MOV,
    UNKNOWN
}
```

## 🧭 GimbalManager

### Control de Gimbal

```kotlin
class GimbalManager private constructor() {
    companion object {
        fun getInstance(): GimbalManager
    }
    
    // Rotar gimbal
    fun rotate(
        rotation: Rotation,
        callback: CommonCallbacks.CompletionCallback
    )
    
    // Resetear gimbal
    fun reset(callback: CommonCallbacks.CompletionCallback)
    
    // Calibrar
    fun calibrate(callback: CommonCallbacks.CompletionCallback)
}
```

### Rotation

```kotlin
data class Rotation(
    val pitch: Float?,      // -90 a 30 grados (típico)
    val roll: Float?,       // -45 a 45 grados
    val yaw: Float?,        // -320 a 320 grados
    val mode: RotationMode = RotationMode.RELATIVE_ANGLE,
    val time: Double = 1.0  // segundos
)

enum class RotationMode {
    ABSOLUTE_ANGLE,     // Ángulo absoluto
    RELATIVE_ANGLE,     // Ángulo relativo al actual
    SPEED               // Velocidad de rotación
}
```

## 🔋 BatteryManager

### Battery Keys

```kotlin
object BatteryKey {
    // Porcentaje de carga
    val CHARGE_REMAINING_IN_PERCENT = "ChargeRemainingInPercent"
    
    // Voltaje
    val VOLTAGE = "Voltage"
    
    // Corriente
    val CURRENT = "Current"
    
    // Temperatura
    val TEMPERATURE = "Temperature"
    
    // Número de celdas
    val CELL_COUNT = "CellCount"
    
    // Vida útil
    val LIFETIME_REMAINING = "LifetimeRemaining"
    
    // Tiempo de vuelo restante
    val FLIGHT_TIME_REMAINING = "FlightTimeRemaining"
    
    // Estado de carga
    val CHARGING_STATE = "ChargingState"
}
```

### Ejemplo: Monitoreo de Batería

```kotlin
val key = BatteryKey.create(0)
    .createChildKey(BatteryKey.CHARGE_REMAINING_IN_PERCENT)

KeyManager.getInstance().listen(key, this, object : KeyListener<Int> {
    override fun onValueChange(oldValue: Int?, newValue: Int?) {
        newValue?.let { charge ->
            Log.d(TAG, "Batería: $charge%")
            
            when {
                charge < 10 -> showCriticalBatteryWarning()
                charge < 20 -> showLowBatteryWarning()
                charge < 30 -> showBatteryWarning()
            }
        }
    }
})
```

## 📡 RTKManager

### RTK Network Service

```kotlin
class RTKManager private constructor() {
    companion object {
        fun getInstance(): RTKManager
    }
    
    // Habilitar RTK
    fun enableRTK(callback: CommonCallbacks.CompletionCallback)
    
    // Deshabilitar RTK
    fun disableRTK(callback: CommonCallbacks.CompletionCallback)
    
    // Conectar servicio de red RTK
    fun startNetworkRTKService(
        config: NetworkServiceConfig,
        callback: CommonCallbacks.CompletionCallback
    )
}
```

### NetworkServiceConfig

```kotlin
data class NetworkServiceConfig(
    val serviceType: RTKServiceType,
    val serverAddress: String,
    val port: Int,
    val username: String,
    val password: String,
    val mountPoint: String
)

enum class RTKServiceType {
    NTRIP,      // NTRIP protocol
    CUSTOM      // Custom service
}
```

## 🗺️ Perception (Sensores)

### PerceptionManager

```kotlin
class PerceptionManager private constructor() {
    companion object {
        fun getInstance(): PerceptionManager
    }
    
    // Obtener información de radar
    fun getRadarInformation(): RadarInformation?
    
    // Listener de obstáculos
    fun addObstacleAvoidanceListener(
        listener: ObstacleAvoidanceListener
    )
}
```

### ObstacleAvoidanceListener

```kotlin
interface ObstacleAvoidanceListener {
    fun onObstacleDetected(
        direction: ObstacleDirection,
        distance: Float  // metros
    )
}

enum class ObstacleDirection {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    UP,
    DOWN
}
```

## 📍 LocationCoordinate

```kotlin
// Coordenada 2D
data class LocationCoordinate2D(
    val latitude: Double,   // -90 a 90
    val longitude: Double   // -180 a 180
)

// Coordenada 3D
data class LocationCoordinate3D(
    val latitude: Double,
    val longitude: Double,
    val altitude: Float     // metros sobre nivel del mar
)
```

## ⚠️ Error Handling

### IDJIError

```kotlin
interface IDJIError {
    fun errorCode(): String
    fun description(): String
    fun toString(): String
}
```

### Códigos de Error Comunes

```kotlin
object DJIError {
    // SDK
    const val SDK_NOT_REGISTERED = "SDK_NOT_REGISTERED"
    const val SDK_INIT_FAILED = "SDK_INIT_FAILED"
    
    // Conexión
    const val PRODUCT_NOT_CONNECTED = "PRODUCT_NOT_CONNECTED"
    const val COMPONENT_NOT_CONNECTED = "COMPONENT_NOT_CONNECTED"
    
    // Operación
    const val OPERATION_NOT_SUPPORTED = "OPERATION_NOT_SUPPORTED"
    const val OPERATION_FAILED = "OPERATION_FAILED"
    const val TIMEOUT = "TIMEOUT"
    
    // Parámetros
    const val INVALID_PARAMETER = "INVALID_PARAMETER"
    const val PARAMETER_OUT_OF_RANGE = "PARAMETER_OUT_OF_RANGE"
    
    // Estado
    const val INVALID_STATE = "INVALID_STATE"
    const val MOTORS_NOT_STARTED = "MOTORS_NOT_STARTED"
    
    // Permisos
    const val PERMISSION_DENIED = "PERMISSION_DENIED"
}
```

## 🎯 CommonCallbacks

```kotlin
object CommonCallbacks {
    // Callback simple
    interface CompletionCallback {
        fun onSuccess()
        fun onFailure(error: IDJIError)
    }
    
    // Callback con resultado
    interface CompletionCallbackWithParam<T> {
        fun onSuccess(result: T)
        fun onFailure(error: IDJIError)
    }
    
    // Callback con progreso
    interface ProgressCallback {
        fun onProgress(current: Long, total: Long)
    }
}
```

## 🎨 UX SDK (dji.v5.ux)

### Widget Base Classes

```kotlin
// Widget base
abstract class ConstraintLayoutWidget : ConstraintLayout {
    abstract fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
    abstract fun reactToModelChanges()
    abstract fun onAttachedToWindow()
    abstract fun onDetachedFromWindow()
}

// Widget con estado
abstract class BaseWidget<T> : ConstraintLayoutWidget() {
    abstract val widgetModel: WidgetModel
    abstract fun updateUI(state: T)
}
```

### Core Widgets (Ver ui-components.md)

Referencia completa de widgets en [ui-components.md](./ui-components.md)

**Principales categorías**:
- FPV Widgets
- Simulator Widgets
- Flight Control Widgets
- Camera Widgets
- Telemetry Widgets
- System Widgets
- Gimbal Widgets
- Map Widgets

## 🛠️ Utilities

### DJILogHelper

```kotlin
object DJILogHelper {
    fun logError(tag: String, message: String)
    fun logWarning(tag: String, message: String)
    fun logInfo(tag: String, message: String)
    fun logDebug(tag: String, message: String)
}
```

### UnitConversionUtil

```kotlin
object UnitConversionUtil {
    // Temperatura
    fun celsiusToFahrenheit(celsius: Float): Float
    fun fahrenheitToCelsius(fahrenheit: Float): Float
    
    // Distancia
    fun metersToFeet(meters: Float): Float
    fun feetToMeters(feet: Float): Float
    
    // Velocidad
    fun mpsToMph(mps: Float): Float  // m/s to mph
    fun mpsToKmh(mps: Float): Float  // m/s to km/h
}
```

## 📖 Best Practices

### 1. Inicialización

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        SDKManager.getInstance().init(this, object : SDKManagerCallback {
            override fun onRegisterSuccess() {
                // SDK listo
            }
            
            override fun onRegisterFailure(error: IDJIError) {
                // Manejo de error
            }
            
            // ... otros callbacks
        })
    }
}
```

### 2. Lifecycle Management

```kotlin
class DroneActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        // Registrar listeners
        SimulatorManager.getInstance().addSimulatorStateListener(simulatorListener)
    }
    
    override fun onPause() {
        // Limpiar listeners
        SimulatorManager.getInstance().removeSimulatorStateListener(simulatorListener)
        super.onPause()
    }
}
```

### 3. Error Handling

```kotlin
fun executeAction() {
    FlightControllerManager.getInstance().startTakeoff { error ->
        if (error == null) {
            Log.d(TAG, "✅ Takeoff successful")
        } else {
            when (error.errorCode()) {
                DJIError.MOTORS_NOT_STARTED -> {
                    // Intentar encender motores primero
                }
                DJIError.INVALID_STATE -> {
                    // Verificar estado del drone
                }
                else -> {
                    Log.e(TAG, "Error: ${error.description()}")
                }
            }
        }
    }
}
```

### 4. Threading

```kotlin
// UI Operations
lifecycleScope.launch(Dispatchers.Main) {
    updateUI()
}

// Heavy Operations
lifecycleScope.launch(Dispatchers.IO) {
    val data = downloadMediaFiles()
    withContext(Dispatchers.Main) {
        displayData(data)
    }
}

// DJI SDK callbacks already run on main thread
```

## 📚 Referencias

- [API Reference V5 Complete](https://developer.dji.com/api-reference-v5/android-api/index.html)
- [API Reference Document](https://developer.dji.com/document/8b1ac49a-ef21-4de5-9f5d-e2ecaa2bf0bb)
- [Tutorial Documentation](https://developer.dji.com/doc/mobile-sdk-tutorial/en/)

## 🔗 Ver También

- [api-reference.md](./api-reference.md) - SimulatorManager detallado
- [sdk-architecture.md](./sdk-architecture.md) - Arquitectura general
- [ui-components.md](./ui-components.md) - Catálogo de widgets
- [troubleshooting.md](./troubleshooting.md) - Solución de problemas
- [faq.md](./faq.md) - Preguntas frecuentes

---

**Última actualización**: 2025-10-30
