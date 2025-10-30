# DJI Mobile SDK V5 - Arquitectura

## 📐 Visión General de la Arquitectura

El DJI Mobile SDK V5 está diseñado con una arquitectura modular que separa las responsabilidades en diferentes capas.

### Capas Principales

```
┌─────────────────────────────────────────┐
│     Application Layer (Tu App)          │
├─────────────────────────────────────────┤
│     UX SDK Layer (UI Components)        │  ← Widgets predefinidos
├─────────────────────────────────────────┤
│     Mobile SDK V5 (Core API)            │  ← Managers & APIs
├─────────────────────────────────────────┤
│     Network Layer                       │  ← Comunicación
├─────────────────────────────────────────┤
│     DJI Aircraft/RC/Camera              │  ← Hardware
└─────────────────────────────────────────┘
```

## 🎯 Componentes Clave del SDK V5

### 1. **SDK Manager** (`SDKManager`)
- Punto de entrada principal
- Gestiona inicialización y registro
- Maneja callbacks de conexión de productos

```kotlin
SDKManager.getInstance().init(context, object : SDKManagerCallback {
    override fun onRegisterSuccess() { }
    override fun onRegisterFailure(error: IDJIError) { }
    override fun onProductConnect(product: BaseProduct) { }
    override fun onProductDisconnect() { }
    override fun onProductChanged(product: BaseProduct) { }
    override fun onComponentChange(key: ComponentKey, oldComponent: BaseComponent?, newComponent: BaseComponent?) { }
    override fun onDatabaseDownloadProgress(current: Long, total: Long) { }
})
```

### 2. **Managers Especializados**
Cada manager controla un aspecto específico del drone:

| Manager | Responsabilidad | Package |
|---------|----------------|---------|
| `SimulatorManager` | Control de simulador | `dji.v5.manager.aircraft.simulator` |
| `FlightControlManager` | Control de vuelo | `dji.v5.manager.aircraft.flightcontrol` |
| `CameraManager` | Control de cámara | `dji.v5.manager.datacenter.camera` |
| `GimbalManager` | Control de gimbal | `dji.v5.manager.aircraft.gimbal` |
| `RTKManager` | Sistema RTK | `dji.v5.manager.aircraft.rtk` |
| `BatteryManager` | Estado de batería | `dji.v5.manager.aircraft.battery` |
| `MediaManager` | Gestión de archivos multimedia | `dji.v5.manager.datacenter.media` |
| `VirtualStickManager` | Control virtual stick | `dji.v5.manager.aircraft.virtualstick` |

### 3. **Key-Value System**
Sistema de acceso a propiedades del drone mediante keys:

```kotlin
KeyManager.getInstance().getValue(
    KeyTools.createKey(FlightControllerKey.KeyAltitude)
) { oldValue, newValue ->
    Log.d(TAG, "Altitude: $newValue")
}
```

### 4. **Data Center**
Gestiona streams de datos en tiempo real:
- Video streaming
- Telemetría
- Logs de vuelo

## 🏗️ Estructura del Proyecto

### Módulos Requeridos

```gradle
dependencies {
    // Core SDK
    implementation 'com.dji:dji-sdk-v5-aircraft:5.16.0'
    compileOnly 'com.dji:dji-sdk-v5-aircraft-provided:5.16.0'
    
    // Network (obligatorio en runtime)
    runtimeOnly 'com.dji:dji-sdk-v5-networkImp:5.16.0'
    
    // UX SDK (opcional, para UI components)
    implementation 'com.dji:dji-sdk-v5-uxsdk:5.16.0'
}
```

## 🔄 Flujo de Inicialización

```
1. Application.onCreate()
   └─> SDKManager.init()
   
2. SDK Registration
   └─> onRegisterSuccess/Failure
   
3. Product Connection
   └─> onProductConnect()
   
4. Component Initialization
   └─> Managers available
   
5. Start Using APIs
   └─> SimulatorManager, FlightControl, etc.
```

## 🎨 UX SDK Components

### Widgets Disponibles

#### Control & Status
- `FPVWidget` - Vista en primera persona
- `SimulatorIndicatorWidget` - Indicador de simulador
- `SimulatorControlWidget` - Control del simulador
- `SystemStatusWidget` - Estado del sistema
- `BatteryWidget` - Estado de batería
- `GPSSignalWidget` - Señal GPS
- `RemoteControlSignalWidget` - Señal RC

#### Camera Controls
- `CameraConfigWidget` - Configuración de cámara
- `ExposureSettingsWidget` - Ajustes de exposición
- `FocalZoomWidget` - Zoom focal
- `CameraCaptureWidget` - Captura foto/video

#### Flight Controls
- `TakeOffWidget` - Despegue
- `ReturnHomeWidget` - Regreso a casa
- `FlightModeWidget` - Modo de vuelo

## 📦 Productos Soportados

### Aircraft (Aviones/Drones)
- **Mavic Series**: Mavic 3, Mavic 3 Enterprise, Mavic 2
- **Phantom Series**: Phantom 4 RTK, Phantom 4 Pro V2.0
- **Inspire Series**: Inspire 2
- **Matrice Series**: M30, M30T, M300 RTK, M350 RTK
- **Agras Series**: T40, T30, T20, T16

### Handheld
- **Osmo Series**: Osmo Action, Osmo Mobile

### Remote Controllers
- RC-N1, RC Pro, RC Plus, Smart Controller Enterprise

## 🔌 Ciclo de Vida de Conexión

```kotlin
// 1. Inicializar SDK
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SDKManager.getInstance().init(this, callback)
    }
}

// 2. Observar conexión de producto
ProductConnectionManager.getInstance().addProductConnectionListener { connected ->
    if (connected) {
        // Producto conectado, inicializar managers
        val product = ProductConnectionManager.getInstance().currentProduct
        Log.d(TAG, "Connected to: ${product?.productType}")
    }
}

// 3. Usar managers específicos
if (SimulatorManager.getInstance().isSimulatorEnabled) {
    // Simulador activo
}
```

## 🛠️ Patrones de Diseño

### Singleton Pattern
Todos los managers usan singleton:
```kotlin
SimulatorManager.getInstance()
FlightControlManager.getInstance()
CameraManager.getInstance()
```

### Observer Pattern
Listeners para eventos:
```kotlin
manager.addListener { data ->
    // React to changes
}
```

### Callback Pattern
Operaciones asíncronas:
```kotlin
manager.doSomething(object : CommonCallbacks.CompletionCallback {
    override fun onSuccess() { }
    override fun onFailure(error: IDJIError) { }
})
```

## 🔒 Permisos Requeridos

```xml
<manifest>
    <!-- Obligatorios -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    
    <!-- Para conectividad USB -->
    <uses-permission android:name="android.permission.USB_PERMISSION" />
    
    <!-- Para ubicación (simulador) -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    
    <!-- Para almacenamiento (media) -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    
    <!-- Para Bluetooth (algunos RC) -->
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
</manifest>
```

## 📊 Threading Model

- **Main Thread**: UI updates, SDK callbacks
- **Background Threads**: Network, file I/O, processing
- **Handler/Looper**: Para operaciones programadas

```kotlin
// Operación en background
Thread {
    // Heavy operation
    runOnUiThread {
        // Update UI
    }
}.start()

// O usar coroutines
lifecycleScope.launch {
    withContext(Dispatchers.IO) {
        // Background work
    }
    // Main thread by default
    updateUI()
}
```

## 🎯 Best Practices

1. **Inicializar SDK en Application.onCreate()**
2. **Esperar onRegisterSuccess antes de usar APIs**
3. **Verificar conexión de producto antes de operaciones**
4. **Limpiar listeners en onDestroy()**
5. **Manejar errores con callbacks apropiados**
6. **Usar simulador para desarrollo sin hardware**
7. **Implementar retry logic para operaciones de red**

## 📚 Referencias

- [SDK Architecture - DeepWiki](https://deepwiki.com/dji-sdk/Mobile-SDK-Android-V5/2-sdk-architecture)
- [Installation Guide - DeepWiki](https://deepwiki.com/dji-sdk/Mobile-SDK-Android-V5/3.1-installation-and-setup)
- [Supported Products](https://deepwiki.com/dji-sdk/Mobile-SDK-Android-V5/1.1-supported-products)

---

**Última actualización**: 2025-10-30
