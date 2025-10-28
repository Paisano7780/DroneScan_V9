# DroneScan - DJI Drone Barcode Scanner

A professional Android application for scanning QR codes and barcodes using a DJI NEO drone with RCN3 remote controller. The app integrates DJI Mobile SDK V5 with ML Kit for real-time barcode detection and GPS-tagged data logging.

## Features

- ✈️ **DJI NEO Drone Integration** - Full support for DJI NEO with RCN3 remote controller
- 📹 **Real-time Video Feed** - Live camera stream from drone
- 🔍 **Barcode Detection** - Automatic detection of QR codes and various barcode formats
- 📸 **Remote Capture** - Confirm scans using the shutter button on RC
- 📍 **GPS Tagging** - All scans include GPS coordinates and altitude
- 💾 **Data Export** - Save scans to JSON and CSV formats
- 📊 **Session Management** - Organize scans into sessions

## Requirements

### Hardware
- DJI NEO Drone
- DJI RCN3 Remote Controller
- Android device with USB OTG support (Android 7.0+)
- USB cable to connect phone to RC

### Software
- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0+)
- JDK 17

## Setup Instructions

### DJI App Configuration ✅

**Your DJI App is already registered and configured:**

- **App Name:** DroneScan
- **Package Name:** `com.dronescan.msdksample`
- **App Key:** `3196948d4ecce3e531187b11` ✅ Already configured in AndroidManifest.xml
- **Platform:** Android (Mobile SDK)
- **Category:** QR and Bar Code Scanner

The App Key is already included in the project, so you can skip the DJI developer registration step!

### Build Project

```bash
# Clone the repository
git clone https://github.com/yourusername/DroneScan_V9.git
cd DroneScan_V9

# Open in Android Studio and sync Gradle
# Build the project
./gradlew build

# Install on device
./gradlew installDebug
```

## Usage

### Initial Setup

1. **Connect Hardware**
   - Connect DJI RCN3 remote controller to Android phone via USB
   - Power on the DJI NEO drone
   - Wait for drone to connect to RC

2. **Launch App**
   - Open DroneScan app
   - Grant all required permissions (Camera, Location, Bluetooth)
   - Wait for SDK initialization and drone connection

### Scanning Workflow

1. **Start Session** - Tap "START SESSION" button
2. **Begin Scanning** - Tap "START SCAN" button
3. **Position Drone** - Fly drone to view barcode/QR code
4. **Capture** - Press **SHUTTER button** on RC when code detected
5. **End Session** - Tap "END SESSION" to save data

## File Structure

```
com.dronescan.msdksample/
├── DroneScanApplication.kt      # Application entry point
├── dji/
│   ├── DJISDKHelper.kt         # SDK initialization
│   ├── VideoStreamManager.kt   # Video stream handling
│   └── RemoteControllerManager.kt  # RC button monitoring
├── barcode/
│   └── BarcodeDetector.kt      # ML Kit barcode detection
├── data/
│   ├── ScannedCode.kt          # Data models
│   └── ScanDataRepository.kt   # Data persistence
├── viewmodel/
│   └── ScanViewModel.kt        # Business logic
├── ui/
│   └── MainActivity.kt         # Main UI
└── utils/
    └── LogUtils.kt             # Logging utilities
```

## Output Files

Scanned data is saved to:
```
/Android/data/com.dronescan.msdksample/files/DroneScan/
```

## Supported Barcode Formats

QR Code, EAN-13, EAN-8, UPC-A, UPC-E, Code-39, Code-93, Code-128, ITF, Codabar, Data Matrix, PDF417, Aztec

## Troubleshooting

### SDK Registration Failed
- Verify DJI App Key in AndroidManifest.xml
- Check internet connection
- Ensure package name matches DJI Developer Portal

### Drone Not Connecting
- Check USB cable connection
- Enable USB debugging
- Restart RC and drone
- Grant all USB permissions

### Barcode Not Detected
- Ensure adequate lighting
- Position drone 0.5-2 meters from barcode
- Hold drone stable
- Check barcode quality

## License

This project is provided as-is for educational and commercial use.

## Support

- DJI SDK: https://developer.dji.com/doc/mobile-sdk-tutorial/en/
- ML Kit: https://developers.google.com/ml-kit/vision/barcode-scanning
