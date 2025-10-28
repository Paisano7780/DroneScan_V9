package com.dronescan.msdksample.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.dronescan.msdksample.barcode.BarcodeDetector
import com.dronescan.msdksample.barcode.DetectedCode
import com.dronescan.msdksample.data.ScanDataRepository
import com.dronescan.msdksample.data.ScanSession
import com.dronescan.msdksample.data.ScannedCode
import com.dronescan.msdksample.dji.RemoteControllerManager
import com.dronescan.msdksample.dji.VideoStreamManager
import com.dronescan.msdksample.utils.LogUtils
import dji.sdk.keyvalue.key.FlightControllerKey
import dji.sdk.keyvalue.value.flightcontroller.GPSSignalLevel
import dji.v5.et.create
import dji.v5.manager.KeyManager
import dji.v5.common.callback.CommonCallbacks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for main scanning activity
 * Coordinates video streaming, barcode detection, and data management
 */
class ScanViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "ScanViewModel"
    }

    // Managers
    private val videoStreamManager = VideoStreamManager()
    private val barcodeDetector = BarcodeDetector()
    private val rcManager = RemoteControllerManager()
    private val dataRepository = ScanDataRepository(application)

    // LiveData
    private val _scanState = MutableLiveData<ScanState>(ScanState.Idle)
    val scanState: LiveData<ScanState> = _scanState

    private val _currentLocation = MutableLiveData<Location?>()
    val currentLocation: LiveData<Location?> = _currentLocation

    private val _scannedCodes = MutableLiveData<List<ScannedCode>>(emptyList())
    val scannedCodes: LiveData<List<ScannedCode>> = _scannedCodes

    private val _sessionInfo = MutableLiveData<String>()
    val sessionInfo: LiveData<String> = _sessionInfo

    // Detect when user should press button
    val detectedCodes: LiveData<List<DetectedCode>> = barcodeDetector.detectedCodes
    val isDetecting: LiveData<Boolean> = barcodeDetector.isDetecting
    val isStreaming: LiveData<Boolean> = videoStreamManager.isStreaming
    val shutterButtonPressed: LiveData<Boolean> = rcManager.shutterButtonPressed

    // Mediator to trigger capture when button pressed and code detected
    private val captureTrigger = MediatorLiveData<Unit>()

    init {
        LogUtils.d(TAG, "ScanViewModel initialized")
        setupCaptureTrigger()
        startGPSMonitoring()
    }

    private fun setupCaptureTrigger() {
        captureTrigger.addSource(shutterButtonPressed) { pressed ->
            if (pressed == true) {
                val codes = detectedCodes.value
                if (!codes.isNullOrEmpty()) {
                    LogUtils.i(TAG, "Shutter pressed with ${codes.size} code(s) detected")
                    captureDetectedCodes(codes)
                } else {
                    LogUtils.w(TAG, "Shutter pressed but no codes detected")
                    _scanState.postValue(ScanState.Error("No code detected. Position drone closer to barcode."))
                }
            }
        }
    }

    private fun startGPSMonitoring() {
        // Monitor GPS location from drone
        KeyManager.getInstance().listen(
            FlightControllerKey.KeyGPSSignalLevel.create(),
            this,
            object : CommonCallbacks.KeyListener<GPSSignalLevel> {
                override fun onValueChange(oldValue: GPSSignalLevel?, newValue: GPSSignalLevel?) {
                    newValue?.let {
                        LogUtils.d(TAG, "GPS Signal: $it")
                    }
                }
            }
        )

        // Get location periodically
        KeyManager.getInstance().listen(
            FlightControllerKey.KeyAircraftLocation.create(),
            this,
            object : CommonCallbacks.KeyListener<dji.sdk.keyvalue.value.common.LocationCoordinate2D> {
                override fun onValueChange(
                    oldValue: dji.sdk.keyvalue.value.common.LocationCoordinate2D?,
                    newValue: dji.sdk.keyvalue.value.common.LocationCoordinate2D?
                ) {
                    newValue?.let { coord ->
                        // Get altitude separately
                        val altitudeKey = FlightControllerKey.KeyAltitude.create()
                        val altitude = KeyManager.getInstance().getValue(altitudeKey)?.toDouble() ?: 0.0
                        
                        val location = Location("DJI").apply {
                            latitude = coord.latitude
                            longitude = coord.longitude
                            this.altitude = altitude
                        }
                        _currentLocation.postValue(location)
                    }
                }
            }
        )
    }

    fun startSession(notes: String? = null) {
        dataRepository.startSession(notes)
        updateSessionInfo()
        _scanState.postValue(ScanState.SessionActive)
        LogUtils.i(TAG, "Session started")
    }

    fun startScanning() {
        LogUtils.i(TAG, "Starting scanning process...")
        
        // Ensure session exists
        if (dataRepository.getCurrentSession() == null) {
            startSession()
        }

        // Start barcode detection
        barcodeDetector.enable()
        
        // Start RC monitoring
        rcManager.startMonitoring()
        
        _scanState.postValue(ScanState.Scanning)
        LogUtils.i(TAG, "Scanning started")
    }

    fun stopScanning() {
        LogUtils.i(TAG, "Stopping scanning...")
        
        barcodeDetector.disable()
        rcManager.stopMonitoring()
        
        _scanState.postValue(ScanState.SessionActive)
        LogUtils.i(TAG, "Scanning stopped")
    }

    fun closeSession(): java.io.File? {
        stopScanning()
        val file = dataRepository.closeSession()
        updateSessionInfo()
        _scanState.postValue(ScanState.Idle)
        LogUtils.i(TAG, "Session closed")
        return file
    }

    private fun captureDetectedCodes(codes: List<DetectedCode>) {
        val location = _currentLocation.value
        val session = dataRepository.getCurrentSession()
        
        if (session == null) {
            LogUtils.e(TAG, "No active session")
            return
        }

        codes.forEach { detected ->
            val scannedCode = dataRepository.addScannedCode(
                value = detected.value,
                format = detected.format,
                location = location,
                notes = null
            )
            
            scannedCode?.let {
                LogUtils.i(TAG, "Captured code: ${it.value} at ${it.getFormattedLocation()}")
            }
        }

        // Update UI
        _scannedCodes.postValue(session.scannedCodes.toList())
        updateSessionInfo()
        _scanState.postValue(ScanState.CodeCaptured(codes.first().value))
    }

    private fun updateSessionInfo() {
        dataRepository.getCurrentSession()?.let { session ->
            _sessionInfo.postValue(session.getFormattedInfo())
        }
    }

    fun setVideoStreamCallback(surface: android.view.Surface, width: Int, height: Int) {
        // Start video stream
        videoStreamManager.startStream(surface, width, height)
        
        // Set up frame callback for barcode detection
        videoStreamManager.setYuvFrameCallback { yuvData, w, h ->
            // Process frame for barcode detection
            barcodeDetector.processYuvFrame(yuvData, w, h, 0)
        }
    }

    fun exportSession(): Pair<java.io.File?, java.io.File?> {
        return dataRepository.exportCurrentSession()
    }

    override fun onCleared() {
        super.onCleared()
        LogUtils.d(TAG, "ViewModel cleared, cleaning up...")
        
        stopScanning()
        videoStreamManager.cleanup()
        barcodeDetector.cleanup()
        
        KeyManager.getInstance().cancelListen(this)
    }
}

/**
 * Sealed class representing scan states
 */
sealed class ScanState {
    object Idle : ScanState()
    object SessionActive : ScanState()
    object Scanning : ScanState()
    data class CodeCaptured(val value: String) : ScanState()
    data class Error(val message: String) : ScanState()
}
