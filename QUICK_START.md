# 🚀 Guía Rápida - DroneScan

## ✅ Tu App Está Lista

**App Key DJI:** `3196948d4ecce3e531187b11` (Ya configurado)  
**Package Name:** `com.dronescan.msdksample`  
**Estado:** ✅ Listo para compilar

---

## 📱 Compilar e Instalar (3 pasos)

### 1️⃣ Abrir en Android Studio
```bash
# En tu máquina local con Android Studio instalado:
# File > Open > Selecciona la carpeta DroneScan_V9
```

### 2️⃣ Conectar Hardware
- ✅ Enciende el **DJI NEO** drone
- ✅ Enciende el **DJI RCN3** remote controller
- ✅ Conecta tu **Android phone** al RC via USB
- ✅ Conecta el phone a la computadora via USB (modo depuración activado)

### 3️⃣ Compilar e Instalar
En Android Studio:
- Click en el botón verde **▶ Run**
- O usa: `./gradlew installDebug` en terminal

---

## 🎮 Cómo Usar la App

### Primera Vez (Solo una vez)
1. **Abre la app** en tu teléfono
2. **Acepta todos los permisos** (Cámara, Ubicación, USB, Bluetooth)
3. **Espera la conexión** al drone (Status: "Connected")

### Escaneo de Códigos
1. **Start Session** - Inicia una nueva sesión de escaneo
2. **Start Scan** - Activa la detección de códigos
3. **Vuela el drone** cerca de los códigos QR/Barcode
4. **Apunta la cámara** al código que quieres escanear
5. **Presiona el botón de foto del RC** cuando veas el código en pantalla
6. **End Session** - Guarda todos los datos

### Archivos Generados
Los archivos se guardan en:
```
/Android/data/com.dronescan.msdksample/files/DroneScan/
- session_YYYYMMDD_HHMMSS.json  (Datos en JSON)
- session_YYYYMMDD_HHMMSS.csv   (Datos en CSV)
```

---

## 🔍 Formatos Soportados

✅ QR Code  
✅ EAN-13 / EAN-8  
✅ UPC-A / UPC-E  
✅ Code-39 / Code-93 / Code-128  
✅ ITF (Interleaved 2 of 5)  
✅ Codabar  
✅ Data Matrix  
✅ PDF417  
✅ Aztec  

---

## ⚠️ Solución de Problemas

### "SDK Registration Failed"
- ✅ Verifica conexión a internet (primera vez)
- ✅ Espera 30-60 segundos y reinicia la app

### "No Drone Connected"
- ✅ Verifica que el drone esté encendido
- ✅ Verifica que el RC esté conectado al teléfono
- ✅ Acepta el permiso USB cuando aparezca

### "No Barcode Detected"
- ✅ Acércate más al código (2-5 metros de distancia)
- ✅ Asegúrate de tener buena iluminación
- ✅ El código debe estar bien enfocado en la cámara
- ✅ Mantén el drone estable por 1-2 segundos

### "No GPS Data"
- ✅ Vuela en exterior (señal GPS)
- ✅ Espera 1-2 minutos para adquisición de satélites
- ✅ Los datos se guardarán sin GPS si no está disponible

---

## 📋 Datos en CSV

Cada línea del CSV contiene:
```
Timestamp, BarcodeType, RawValue, DisplayValue, Latitude, Longitude, Altitude
```

Ejemplo:
```csv
2025-10-27 15:30:45,QR_CODE,https://example.com,https://example.com,37.7749,-122.4194,50.5
2025-10-27 15:31:12,EAN_13,1234567890123,1234567890123,37.7750,-122.4195,51.2
```

---

## 🛠️ Comandos de Terminal

```bash
# Compilar APK Debug
./gradlew assembleDebug

# Instalar en dispositivo
./gradlew installDebug

# Ver logs en tiempo real
adb logcat -s DroneScan:*

# Limpiar proyecto
./gradlew clean
```

---

## 📞 Soporte DJI

- **Portal de Desarrolladores:** https://developer.dji.com/
- **Documentación SDK:** https://developer.dji.com/doc/mobile-sdk-tutorial/en/
- **Forum:** https://forum.dji.com/forum-139-1.html

---

**¡Listo para volar y escanear! 🚁📦**
