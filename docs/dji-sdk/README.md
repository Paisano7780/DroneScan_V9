# DJI Mobile SDK V5 - Knowledge Stack

Documentación compilada del DJI Mobile SDK V5 para Android.

## 📚 Índice de Documentación

1. **[API Reference Complete](./api-reference-complete.md)** - 🆕 Referencia completa de toda la API V5 (Managers, Keys, Callbacks)
2. **[API Reference](./api-reference.md)** - Documentación completa de SimulatorManager y APIs principales
3. **[Troubleshooting](./troubleshooting.md)** - Soluciones a errores comunes y problemas de compilación
4. **[SDK Architecture](./sdk-architecture.md)** - Arquitectura, componentes, managers y patrones de diseño
5. **[Installation Guide](./installation-guide.md)** - Guía completa de instalación y configuración paso a paso
6. **[UI Components](./ui-components.md)** - Catálogo de widgets UX SDK con ejemplos de uso
7. **[FAQ](./faq.md)** - Preguntas frecuentes y respuestas sobre desarrollo con DJI SDK

## 🔗 Links de Documentación Oficial Revisados

### Documentación Core
1. [Mobile SDK V5 Tutorial](https://developer.dji.com/doc/mobile-sdk-tutorial/en/)
2. [API Reference V5](https://developer.dji.com/document/8b1ac49a-ef21-4de5-9f5d-e2ecaa2bf0bb)
3. [SimulatorManager API](https://developer.dji.com/document/8b1ac49a-ef21-4de5-9f5d-e2ecaa2bf0bb?pathId=60eb4e0e)

### DeepWiki Resources
4. [SDK Overview](https://deepwiki.com/dji-sdk/Mobile-SDK-Android-V5/1.-overview)
5. [Installation & Setup](https://deepwiki.com/dji-sdk/Mobile-SDK-Android-V5/3.1-installation-and-setup)
6. [UX SDK Components](https://deepwiki.com/dji-sdk/Mobile-SDK-Android-V5/ux-sdk-introduction)
7. [Architecture Design](https://deepwiki.com/dji-sdk/Mobile-SDK-Android-V5/2.-architectural-design)
8. [Supported Products](https://deepwiki.com/dji-sdk/Mobile-SDK-Android-V5/support-products)

### Tutorials & GitHub
9. [Simulator Demo Tutorial](https://developer.dji.com/doc/mobile-sdk-tutorial/en/quick-start/simulator-demo.html)
10. [FAQ Official](https://developer.dji.com/doc/mobile-sdk-tutorial/en/faq/index.html)
11. [Sample Code GitHub](https://github.com/dji-sdk/Mobile-SDK-Android-V5/tree/main/SampleCode-V5)
12. [Media Manager Tutorial](https://developer.dji.com/doc/mobile-sdk-tutorial/en/function-set/media-manager.html)
13. [Mobile SDK Android V5 Repo](https://github.com/dji-sdk/Mobile-SDK-Android-V5)
14. [Pilot2 Sample Code](https://github.com/dji-sdk/Mobile-SDK-Android-V5/tree/main/SampleCode-V5/android-sdk-v5-sample)
15. **[API Reference V5 Index](https://developer.dji.com/api-reference-v5/android-api/index.html)** 🆕

### Legacy V4 Docs (Referencias históricas)
16. [Sample Code Index V4](https://developer.dji.com/mobile-sdk/documentation/sample-code/index.html)
17. [Android Simulator Demo V4](https://developer.dji.com/mobile-sdk/documentation/android-tutorials/SimulatorDemo.html)
18. [iOS Simulator Demo V4](https://developer.dji.com/mobile-sdk/documentation/ios-tutorials/SimulatorDemo.html)
19. [Activation & Binding V4](https://developer.dji.com/mobile-sdk/documentation/android-tutorials/ActivationAndBinding.html)
20. [UX SDK Demo V4](https://developer.dji.com/mobile-sdk/documentation/android-tutorials/UXSDKDemo.html)
21. [DULFPVWidget API V4](https://developer.dji.com/api-reference/android-uilib-api/Widgets/DULFPVWidget.html)

## 📝 Contenido por Documento

### 1. API Reference Complete (NUEVO)
- **SDKManager**: Inicialización, registro, callbacks
- **KeyManager**: Sistema Key-Value completo, getValue/setValue/listen
- **SimulatorManager**: Quick reference
- **FlightControllerManager**: Takeoff, landing, RTH, Virtual Stick
- **CameraManager**: Captura, modos, settings keys
- **MediaFileListManager**: Download/upload/delete archivos
- **GimbalManager**: Rotación, reset, calibración
- **BatteryManager**: Monitoreo completo de batería
- **RTKManager**: Network RTK service
- **PerceptionManager**: Sensores de obstáculos
- **LocationCoordinate**: 2D/3D coordinates
- **Error Handling**: IDJIError, códigos comunes
- **CommonCallbacks**: Todas las interfaces de callback
- **UX SDK**: Widget base classes
- **Utilities**: Logging, conversiones de unidades
- **Best Practices**: Inicialización, lifecycle, threading

### 2. Troubleshooting
- 7 errores de compilación resueltos
- Smart cast issues
- Unresolved reference
- Dependency configuration
- Runtime errors

### 3. SDK Architecture
- Arquitectura en capas (Application → SDK → Network → Hardware)
- 8 Managers especializados
- Key-Value system explicado
- UX SDK widgets catalog
- Productos soportados por familia
- Connection lifecycle
- Design patterns (Singleton, Observer, Callback)
- Threading model y coroutines
- Best practices

### 4. Installation Guide
- Requisitos previos (software y hardware)
- Obtención de App Key desde DJI Developer
- Configuración Gradle (project y app level)
- AndroidManifest.xml completo con permisos
- accessory_filter.xml para USB
- Inicialización de SDK en Application class
- Modo simulador sin hardware
- Troubleshooting de instalación
- Estructura de archivos recomendada
- Checklist de setup completo

### 5. UI Components
- FPV & Video Widgets (FPVWidget, FPVInteractionWidget)
- Simulator Widgets (Indicator, Control)
- Flight Control Widgets (TakeOff, ReturnHome, Landing)
- Camera Widgets (Capture, Settings, Exposure)
- Telemetry Widgets (Altitude, Distance, Speed, Battery, GPS, Compass)
- System Widgets (FlightMode, RC Signal, SystemStatus)
- Gimbal Widgets (Control, FineTune)
- Map Widget
- Layout completo de ejemplo funcional
- Customización (colores, tamaños, visibility)
- Widget state listeners
- Best practices de performance

### 6. FAQ
- Inicio y configuración (App Key, simulador, V4 vs V5, versiones Android)
- Problemas de compilación (App Key invalid, network library, dex files, smart cast)
- Conexión y hardware (USB/WiFi, detection issues)
- Control de vuelo (takeoff, manual control, waypoints)
- Cámara (photo, video, settings, stream)
- GPS y navegación (location, RTK)
- Batería (monitoring)
- Testing (instrumented tests, CI/CD, Firebase Test Lab)
- Debugging (logs, error codes)
- Performance optimization
- Productos soportados
- Seguridad y permisos

### Versión SDK Actual
- DJI SDK V5: `5.16.0`
- Gradle: Ver `app/build.gradle`
- MinSDK: 24, TargetSDK: 34

### Dependencias Críticas
```gradle
implementation 'com.dji:dji-sdk-v5-aircraft:5.16.0'
compileOnly 'com.dji:dji-sdk-v5-aircraft-provided:5.16.0'
runtimeOnly 'com.dji:dji-sdk-v5-networkImp:5.16.0'
```

## 🐛 Problemas Resueltos

### 1. Smart Cast Error en SimulatorManager
**Problema**: Kotlin no puede hacer smart cast de propiedades mutables
**Solución**: Usar variables locales inmutables antes de pasar a métodos DJI

```kotlin
// ❌ Incorrecto
DJISimulatorManager.getInstance().addSimulatorStateListener(simulatorStatusListener)

// ✅ Correcto
val listener = simulatorStatusListener
if (listener != null) {
    DJISimulatorManager.getInstance().addSimulatorStateListener(listener)
}
```

### 2. API Method Names (V5)
**Cambios de V4 a V5**:
- `start()` → `enableSimulator(InitializationSettings, callback)`
- `stop()` → `disableSimulator(callback)`
- `addSimulatorStatusListener()` → `addSimulatorStateListener()`
- `state.altitude` → `state.positionZ`
- `state.areMotorsOn` → `state.areMotorsOn()` (método, no propiedad)

## 📝 Últimas Actualizaciones

**2025-10-30**:
- ✅ Creado api-reference-complete.md con toda la API V5 (~600 líneas)
- ✅ Agregadas dependencias androidTest en build.gradle
- ✅ Documentación expandida: 7 archivos, ~2500 líneas
- ✅ 21 links oficiales procesados e indexados
- ✅ Corregido SimulatorManager.kt con API correcta de v5
- ✅ Compilación local exitosa (BUILD SUCCESSFUL)
- 🔄 Próximo: Commit documentación + fix build.gradle

---

**Mantenido por**: Desarrollo DroneScan V9  
**Última revisión**: 2025-10-30  
**Documentos**: 7 archivos | **Líneas**: ~2500 | **Links**: 21
