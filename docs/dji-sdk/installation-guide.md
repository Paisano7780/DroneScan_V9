# Guía de Instalación y Setup - DJI Mobile SDK V5

## 🚀 Quick Start

### Paso 1: Requisitos Previos

**Software**:
- Android Studio Arctic Fox o superior
- JDK 11 o superior
- Gradle 7.0+
- MinSDK: 24 (Android 7.0)
- TargetSDK: 34 (Android 14)
- Kotlin 1.8+ o Java 8+

**Hardware** (opcional):
- Drone DJI compatible
- Control remoto DJI
- Cable USB

### Paso 2: Crear Cuenta DJI Developer

1. Ir a [DJI Developer](https://developer.dji.com/)
2. Crear cuenta o iniciar sesión
3. Ir a "User Center" → "Apps"
4. Crear nueva aplicación
5. Obtener **App Key**

### Paso 3: Configurar Gradle

#### Project-level `build.gradle`
```gradle
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.1.0'
        classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0'
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

#### App-level `build.gradle`
```gradle
android {
    namespace 'com.dronescan.msdksample'
    compileSdk 34

    defaultConfig {
        applicationId "com.dronescan.msdksample"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
        
        multiDexEnabled true
        
        // Opcional: para tests
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = '11'
    }

    packagingOptions {
        pickFirst 'lib/*/libdjivideo.so'
        pickFirst 'lib/*/libSDKRelativeJNI.so'
        pickFirst 'lib/*/libFlyForbid.so'
        pickFirst 'lib/*/libduml_vision_bokeh.so'
        pickFirst 'lib/*/libyuv2.so'
        pickFirst 'lib/*/libc++_shared.so'
        pickFirst 'lib/*/libFRCorkscrew.so'
        pickFirst 'lib/*/libUpgradeVerify.so'
        pickFirst 'lib/*/libFlyForbid.so'
        pickFirst 'lib/*/libfly_safe_database.so'
        exclude 'META-INF/proguard/androidx-*.pro'
        exclude 'META-INF/rxjava.properties'
    }
}

dependencies {
    // Core DJI SDK V5
    implementation 'com.dji:dji-sdk-v5-aircraft:5.16.0'
    compileOnly 'com.dji:dji-sdk-v5-aircraft-provided:5.16.0'
    
    // Network implementation (OBLIGATORIO en runtime)
    runtimeOnly 'com.dji:dji-sdk-v5-networkImp:5.16.0'
    
    // UX SDK (opcional, pero recomendado para UI)
    implementation 'com.dji:dji-sdk-v5-uxsdk:5.16.0'
    
    // AndroidX
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'com.google.android.material:material:1.11.0'
    
    // Lifecycle
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    
    // Para Tests Instrumentados
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    androidTestImplementation 'com.dji:dji-sdk-v5-aircraft:5.16.0'
    androidTestCompileOnly 'com.dji:dji-sdk-v5-aircraft-provided:5.16.0'
}
```

### Paso 4: Configurar AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- Permisos obligatorios -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    
    <!-- Para USB connection -->
    <uses-permission android:name="android.permission.USB_PERMISSION" />
    
    <!-- Para ubicación -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    
    <!-- Para almacenamiento (Android 12 y anteriores) -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="32"
        tools:ignore="ScopedStorage" />
    
    <!-- Para Android 13+ -->
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    <uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
    
    <!-- Para Bluetooth (algunos RC) -->
    <uses-permission android:name="android.permission.BLUETOOTH" 
        android:maxSdkVersion="30" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" 
        android:maxSdkVersion="30" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />

    <!-- Features -->
    <uses-feature android:name="android.hardware.usb.host" android:required="false" />
    <uses-feature android:name="android.hardware.usb.accessory" android:required="true" />

    <application
        android:name=".DroneScanApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.AppCompat.Light"
        android:usesCleartextTraffic="true"
        tools:targetApi="31">

        <!-- 🔑 DJI App Key (REEMPLAZAR CON TU KEY) -->
        <meta-data
            android:name="com.dji.sdk.API_KEY"
            android:value="TU_APP_KEY_AQUI" />

        <!-- USB Connection -->
        <uses-library android:name="com.android.future.usb.accessory" />
        <uses-library android:name="org.apache.http.legacy"
            android:required="false" />

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:screenOrientation="landscape"
            android:configChanges="orientation|screenSize|keyboardHidden">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            
            <!-- USB Attachment para DJI -->
            <intent-filter>
                <action android:name="android.hardware.usb.action.USB_ACCESSORY_ATTACHED" />
            </intent-filter>
            <meta-data
                android:name="android.hardware.usb.action.USB_ACCESSORY_ATTACHED"
                android:resource="@xml/accessory_filter" />
        </activity>

    </application>

</manifest>
```

### Paso 5: Crear accessory_filter.xml

Crear archivo `res/xml/accessory_filter.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <usb-accessory
        android:manufacturer="DJI"
        android:model=".*" />
</resources>
```

### Paso 6: Inicializar SDK en Application

```kotlin
package com.dronescan.msdksample

import android.app.Application
import android.util.Log
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.SDKManager
import dji.v5.manager.interfaces.SDKManagerCallback

class DroneScanApplication : Application() {

    companion object {
        private const val TAG = "DroneScanApp"
    }

    override fun onCreate() {
        super.onCreate()
        
        // Inicializar DJI SDK
        SDKManager.getInstance().init(this, object : SDKManagerCallback {
            override fun onRegisterSuccess() {
                Log.d(TAG, "✅ SDK registered successfully")
            }

            override fun onRegisterFailure(error: IDJIError) {
                Log.e(TAG, "❌ SDK registration failed: ${error.description()}")
            }

            override fun onProductConnect(productId: Int) {
                Log.d(TAG, "🔌 Product connected: $productId")
            }

            override fun onProductDisconnect(productId: Int) {
                Log.d(TAG, "🔌 Product disconnected: $productId")
            }

            override fun onProductChanged(productId: Int) {
                Log.d(TAG, "🔄 Product changed: $productId")
            }

            override fun onInitProcess(event: SDKInitEvent, totalProcess: Int) {
                when (event) {
                    SDKInitEvent.INITIALIZE_STARTED -> 
                        Log.d(TAG, "⏳ Initialization started")
                    SDKInitEvent.DATABASE_DOWNLAND_STARTED -> 
                        Log.d(TAG, "⬇️ Database download started")
                    SDKInitEvent.DATABASE_DOWNLAND_FINISHED -> 
                        Log.d(TAG, "✅ Database download finished")
                    else -> 
                        Log.d(TAG, "Init event: $event, progress: $totalProcess")
                }
            }

            override fun onDatabaseDownloadProgress(current: Long, total: Long) {
                val progress = (current * 100 / total).toInt()
                Log.d(TAG, "📦 Database download: $progress% ($current/$total)")
            }
        })
    }
}
```

### Paso 7: Verificar Instalación

Crear test simple en MainActivity:

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Verificar registro SDK
        checkSDKRegistration()
    }
    
    private fun checkSDKRegistration() {
        val isRegistered = SDKManager.getInstance().isRegistered
        Log.d(TAG, "SDK Registered: $isRegistered")
        
        if (isRegistered) {
            Toast.makeText(this, "✅ SDK Ready!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "⚠️ SDK Not Registered", Toast.LENGTH_LONG).show()
        }
    }
}
```

## 🧪 Modo Simulador (Sin Hardware)

Para desarrollo sin drone físico:

```kotlin
// En tu Activity o Fragment
private fun startSimulator() {
    val location = LocationCoordinate2D(22.5431, 114.0579) // Shenzhen
    val settings = InitializationSettings.createInstance(location, 12)
    
    SimulatorManager.getInstance().enableSimulator(settings) { error ->
        if (error == null) {
            Log.d(TAG, "✅ Simulator started")
        } else {
            Log.e(TAG, "❌ Simulator failed: ${error.description()}")
        }
    }
}
```

## 🔧 Troubleshooting Común

### Error: "App key is invalid"
- Verificar que la App Key en AndroidManifest.xml sea correcta
- Verificar que el package name coincida con el registrado en DJI Developer

### Error: "Network library not found"
- Agregar `runtimeOnly 'com.dji:dji-sdk-v5-networkImp:5.16.0'`

### Error: "Multiple dex files define..."
```gradle
packagingOptions {
    pickFirst 'lib/*/libdjivideo.so'
    // ... agregar todos los pickFirst necesarios
}
```

### Error: "Unable to find explicit activity class"
- Verificar que el `android:name` en `<application>` apunte a tu Application class

## 📦 Estructura de Archivos Recomendada

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/dronescan/msdksample/
│   │   │   ├── DroneScanApplication.kt
│   │   │   ├── MainActivity.kt
│   │   │   ├── dji/
│   │   │   │   ├── SimulatorManager.kt
│   │   │   │   ├── FlightControlHelper.kt
│   │   │   │   └── CameraHelper.kt
│   │   │   └── utils/
│   │   │       └── LogUtils.kt
│   │   ├── res/
│   │   │   ├── xml/
│   │   │   │   └── accessory_filter.xml
│   │   │   ├── layout/
│   │   │   └── values/
│   │   └── AndroidManifest.xml
│   └── androidTest/
│       └── java/com/dronescan/msdksample/
│           └── BasicInstrumentedTest.kt
└── build.gradle
```

## 🎯 Checklist de Setup

- [ ] Cuenta DJI Developer creada
- [ ] App Key obtenida
- [ ] Gradle configurado con dependencias correctas
- [ ] AndroidManifest.xml con permisos y App Key
- [ ] accessory_filter.xml creado
- [ ] Application class inicializa SDK
- [ ] packagingOptions configurado
- [ ] minSDK >= 24, targetSDK = 34
- [ ] multiDexEnabled = true

## 📚 Referencias

- [Installation Guide - DeepWiki](https://deepwiki.com/dji-sdk/Mobile-SDK-Android-V5/3.1-installation-and-setup)
- [DJI Developer Portal](https://developer.dji.com/)
- [Sample Code](https://github.com/dji-sdk/Mobile-SDK-Android-V5/tree/main/SampleCode-V5)

---

**Última actualización**: 2025-10-30
