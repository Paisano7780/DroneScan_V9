# Componentes UI - DJI Mobile SDK V5

## 📱 UX SDK Overview

El DJI UX SDK V5 proporciona widgets pre-construidos para crear interfaces de control de drones rápidamente. Basado en Jetpack Compose y View system.

## 🎨 Categorías de Widgets

### 1. FPV & Video Widgets

#### FPVWidget
Widget principal para mostrar el feed de video de la cámara.

```kotlin
// XML Layout
<dji.v5.ux.core.widget.fpv.FPVWidget
    android:id="@+id/fpvWidget"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />

// Código Kotlin
val fpvWidget = findViewById<FPVWidget>(R.id.fpvWidget)

// Configurar decode type
fpvWidget.setVideoDecodeType(VideoDecodeType.HARDWARE)

// Listener de estado
fpvWidget.setVideoViewStatusChangeListener { isNormal, width, height ->
    if (isNormal) {
        Log.d(TAG, "Video: ${width}x${height}")
    }
}
```

**Características**:
- Decodificación hardware/software
- Auto-resize
- Aspect ratio automático
- Gesture control (zoom, movimiento)

#### FPVInteractionWidget
FPV con controles táctiles para spot metering y foco.

```kotlin
<dji.v5.ux.core.widget.fpv.FPVInteractionWidget
    android:id="@+id/fpvInteractionWidget"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:uxsdk_enable_spot_metering="true"
    app:uxsdk_enable_tap_to_focus="true" />
```

### 2. Simulator Widgets

#### SimulatorIndicatorWidget
Muestra estado del simulador (activado/desactivado).

```kotlin
<dji.v5.ux.core.widget.simulator.SimulatorIndicatorWidget
    android:id="@+id/simulatorIndicator"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:uxsdk_icon_size="24dp"
    app:uxsdk_background_color="@android:color/transparent" />
```

#### SimulatorControlWidget
Control completo del simulador con ubicación y frecuencia de actualización.

```kotlin
<dji.v5.ux.core.widget.simulator.SimulatorControlWidget
    android:id="@+id/simulatorControl"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uxsdk_show_location_input="true"
    app:uxsdk_show_update_frequency="true" />

// Programáticamente
val simulatorWidget = findViewById<SimulatorControlWidget>(R.id.simulatorControl)

// Listener de eventos
simulatorWidget.setSimulatorActionListener(object : SimulatorActionListener {
    override fun onSimulatorStarted() {
        Log.d(TAG, "✅ Simulator started")
    }
    
    override fun onSimulatorStopped() {
        Log.d(TAG, "⛔ Simulator stopped")
    }
    
    override fun onSimulatorError(error: IDJIError) {
        Log.e(TAG, "❌ Error: ${error.description()}")
    }
})
```

### 3. Flight Control Widgets

#### TakeOffWidget
Botón de despegue con confirmación.

```kotlin
<dji.v5.ux.core.widget.takeoff.TakeOffWidget
    android:id="@+id/takeOffWidget"
    android:layout_width="60dp"
    android:layout_height="60dp"
    app:uxsdk_icon_tint="@color/white"
    app:uxsdk_background_color="@color/blue" />
```

#### ReturnHomeWidget
Botón RTH (Return to Home).

```kotlin
<dji.v5.ux.core.widget.rth.ReturnHomeWidget
    android:id="@+id/rthWidget"
    android:layout_width="60dp"
    android:layout_height="60dp"
    app:uxsdk_icon_tint="@color/white"
    app:uxsdk_background_color="@color/orange" />
```

#### AutoLandingWidget
Landing automático.

```kotlin
<dji.v5.ux.core.widget.landing.AutoLandingWidget
    android:id="@+id/landingWidget"
    android:layout_width="60dp"
    android:layout_height="60dp" />
```

### 4. Camera Widgets

#### CameraCaptureWidget
Botón de captura de foto/video.

```kotlin
<dji.v5.ux.core.widget.camera.CameraCaptureWidget
    android:id="@+id/cameraCaptureWidget"
    android:layout_width="80dp"
    android:layout_height="80dp"
    app:uxsdk_capture_mode="PHOTO_AND_VIDEO" />

// Listener
cameraCapture.setCaptureActionListener(object : CaptureActionListener {
    override fun onPhotoCapture() {
        Log.d(TAG, "📸 Photo captured")
    }
    
    override fun onVideoRecordStart() {
        Log.d(TAG, "🎥 Recording started")
    }
    
    override fun onVideoRecordStop(videoPath: String?) {
        Log.d(TAG, "⏹️ Recording stopped: $videoPath")
    }
})
```

#### CameraSettingsWidget
Panel de configuración de cámara.

```kotlin
<dji.v5.ux.core.widget.camera.CameraSettingsWidget
    android:id="@+id/cameraSettings"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uxsdk_show_iso="true"
    app:uxsdk_show_shutter="true"
    app:uxsdk_show_aperture="true"
    app:uxsdk_show_exposure="true" />
```

#### ExposureSettingsWidget
Control de exposición (ISO, Shutter Speed).

```kotlin
<dji.v5.ux.core.widget.camera.ExposureSettingsWidget
    android:id="@+id/exposureSettings"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

### 5. Telemetry Widgets

#### AltitudeWidget
Muestra altitud actual.

```kotlin
<dji.v5.ux.core.widget.altitude.AltitudeWidget
    android:id="@+id/altitudeWidget"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:uxsdk_unit_type="METRIC"
    app:uxsdk_text_color="@color/white"
    app:uxsdk_text_size="16sp" />
```

#### DistanceWidget
Distancia al home point.

```kotlin
<dji.v5.ux.core.widget.distance.DistanceWidget
    android:id="@+id/distanceWidget"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:uxsdk_unit_type="METRIC" />
```

#### SpeedWidget
Velocidad horizontal/vertical.

```kotlin
<dji.v5.ux.core.widget.speed.SpeedWidget
    android:id="@+id/speedWidget"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:uxsdk_show_horizontal_speed="true"
    app:uxsdk_show_vertical_speed="true" />
```

#### BatteryWidget
Estado de batería con porcentaje.

```kotlin
<dji.v5.ux.core.widget.battery.BatteryWidget
    android:id="@+id/batteryWidget"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:uxsdk_show_percentage="true"
    app:uxsdk_show_voltage="false" />
```

#### GPSSignalWidget
Indicador de señal GPS/RTK.

```kotlin
<dji.v5.ux.core.widget.gps.GPSSignalWidget
    android:id="@+id/gpsWidget"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:uxsdk_show_satellite_count="true" />
```

#### CompassWidget
Brújula con dirección.

```kotlin
<dji.v5.ux.core.widget.compass.CompassWidget
    android:id="@+id/compassWidget"
    android:layout_width="80dp"
    android:layout_height="80dp"
    app:uxsdk_north_color="@color/red"
    app:uxsdk_heading_color="@color/blue" />
```

### 6. System Widgets

#### FlightModeWidget
Modo de vuelo actual (P-Mode, S-Mode, etc.).

```kotlin
<dji.v5.ux.core.widget.flightmode.FlightModeWidget
    android:id="@+id/flightModeWidget"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:uxsdk_text_color="@color/white"
    app:uxsdk_background_color="@color/black" />
```

#### RemoteControlSignalWidget
Señal RC (control remoto).

```kotlin
<dji.v5.ux.core.widget.rc.RemoteControlSignalWidget
    android:id="@+id/rcSignalWidget"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

#### SystemStatusWidget
Lista de advertencias y errores del sistema.

```kotlin
<dji.v5.ux.core.widget.systemstatus.SystemStatusWidget
    android:id="@+id/systemStatusWidget"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uxsdk_max_items="5" />
```

### 7. Gimbal Widgets

#### GimbalWidget
Control de gimbal (pitch, yaw, roll).

```kotlin
<dji.v5.ux.core.widget.gimbal.GimbalWidget
    android:id="@+id/gimbalWidget"
    android:layout_width="100dp"
    android:layout_height="100dp"
    app:uxsdk_enable_pitch_control="true"
    app:uxsdk_enable_yaw_control="true" />
```

#### GimbalFineTuneWidget
Ajuste fino de gimbal.

```kotlin
<dji.v5.ux.core.widget.gimbal.GimbalFineTuneWidget
    android:id="@+id/gimbalFineTune"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

### 8. Map Widgets

#### MapWidget
Mapa con ubicación del drone y home point.

```kotlin
<dji.v5.ux.core.widget.map.MapWidget
    android:id="@+id/mapWidget"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    app:uxsdk_map_provider="GOOGLE"
    app:uxsdk_show_home_point="true"
    app:uxsdk_show_aircraft="true"
    app:uxsdk_show_flight_path="true" />

// Configurar en código
mapWidget.setMapProvider(MapProvider.GOOGLE)
mapWidget.setShowFlightPath(true)
mapWidget.setCenterToAircraft(true)
```

## 🏗️ Layout Completo de Ejemplo

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- FPV Video Feed -->
    <dji.v5.ux.core.widget.fpv.FPVInteractionWidget
        android:id="@+id/fpvWidget"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Top Bar -->
    <LinearLayout
        android:id="@+id/topBar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="8dp"
        android:background="#80000000"
        app:layout_constraintTop_toTopOf="parent">

        <dji.v5.ux.core.widget.flightmode.FlightModeWidget
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

        <dji.v5.ux.core.widget.simulator.SimulatorIndicatorWidget
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="8dp" />

        <Space
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:layout_weight="1" />

        <dji.v5.ux.core.widget.gps.GPSSignalWidget
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

        <dji.v5.ux.core.widget.rc.RemoteControlSignalWidget
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="8dp" />

        <dji.v5.ux.core.widget.battery.BatteryWidget
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="8dp" />

    </LinearLayout>

    <!-- Left Telemetry -->
    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp"
        app:layout_constraintTop_toBottomOf="@id/topBar"
        app:layout_constraintStart_toStartOf="parent">

        <dji.v5.ux.core.widget.altitude.AltitudeWidget
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

        <dji.v5.ux.core.widget.speed.SpeedWidget
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp" />

        <dji.v5.ux.core.widget.distance.DistanceWidget
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp" />

    </LinearLayout>

    <!-- Right Compass -->
    <dji.v5.ux.core.widget.compass.CompassWidget
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:layout_margin="16dp"
        app:layout_constraintTop_toBottomOf="@id/topBar"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Bottom Control Bar -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="16dp"
        android:gravity="center"
        android:background="#80000000"
        app:layout_constraintBottom_toBottomOf="parent">

        <dji.v5.ux.core.widget.takeoff.TakeOffWidget
            android:layout_width="60dp"
            android:layout_height="60dp" />

        <dji.v5.ux.core.widget.camera.CameraCaptureWidget
            android:id="@+id/cameraCaptureWidget"
            android:layout_width="80dp"
            android:layout_height="80dp"
            android:layout_marginHorizontal="16dp" />

        <dji.v5.ux.core.widget.rth.ReturnHomeWidget
            android:layout_width="60dp"
            android:layout_height="60dp" />

    </LinearLayout>

</androidx.constraintlayout.widget.ConstraintLayout>
```

## 🎨 Customización de Widgets

### Cambiar Colores
```kotlin
widget.setIconTintColor(Color.WHITE)
widget.setBackgroundColor(Color.parseColor("#80000000"))
widget.setTextColor(Color.YELLOW)
```

### Cambiar Tamaño
```kotlin
widget.setIconSize(resources.getDimensionPixelSize(R.dimen.icon_large))
widget.setTextSize(16f)
```

### Ocultar/Mostrar Elementos
```kotlin
widget.setShowPercentage(true)
widget.setShowVoltage(false)
widget.setShowSatelliteCount(true)
```

## 🔗 Widget State Listeners

Muchos widgets emiten estados que puedes observar:

```kotlin
// Battery
batteryWidget.batteryState.observe(this) { state ->
    Log.d(TAG, "Battery: ${state.chargeRemainingInPercent}%")
}

// Altitude
altitudeWidget.altitudeState.observe(this) { state ->
    Log.d(TAG, "Altitude: ${state.altitude} m")
}

// GPS
gpsWidget.gpsSignalState.observe(this) { state ->
    Log.d(TAG, "Satellites: ${state.satelliteCount}")
}
```

## 🏆 Best Practices

1. **Performance**: Usa FPVWidget en hardware decode para mejor rendimiento
2. **Thread Safety**: Todos los widgets deben ser manipulados en UI thread
3. **Lifecycle**: Los widgets se vinculan automáticamente al lifecycle de Activity/Fragment
4. **Memory**: Limpia listeners en `onDestroy()` si implementas custom listeners
5. **Theme**: Define colores en `themes.xml` para consistencia

## 📦 Dependency UX SDK

```gradle
dependencies {
    implementation 'com.dji:dji-sdk-v5-uxsdk:5.16.0'
}
```

## 📚 Referencias

- [UX SDK Documentation - DeepWiki](https://deepwiki.com/dji-sdk/Mobile-SDK-Android-V5/ux-sdk-introduction)
- [Widget Catalog](https://developer.dji.com/doc/mobile-sdk-tutorial/en/ui-sdk/android-widgets.html)
- [Sample Code - DefaultLayout](https://github.com/dji-sdk/Mobile-SDK-Android-V5/tree/main/SampleCode-V5/android-sdk-v5-uxsdk)

---

**Última actualización**: 2025-10-30
