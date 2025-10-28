package com.dronescan.msdksample.dji

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dji.v5.manager.KeyManager
import dji.v5.common.callback.CommonCallbacks
import dji.sdk.keyvalue.key.KeyCameraShutterCommand
import dji.sdk.keyvalue.key.KeyCameraRecording
import dji.sdk.keyvalue.value.camera.CameraShutterCommand
import dji.sdk.keyvalue.value.camera.CameraRecordingState
import dji.v5.et.create
import com.dronescan.msdksample.utils.LogUtils

/**
 * Manages remote controller button events
 * Specifically monitors the shutter/photo button press for barcode capture confirmation
 */
class RemoteControllerManager {

    companion object {
        private const val TAG = "RemoteControllerManager"
    }

    // LiveData for shutter button state
    private val _shutterButtonPressed = MutableLiveData<Boolean>()
    val shutterButtonPressed: LiveData<Boolean> = _shutterButtonPressed
    
    // LiveData for record button state
    private val _recordButtonPressed = MutableLiveData<Boolean>()
    val recordButtonPressed: LiveData<Boolean> = _recordButtonPressed
    
    private var isMonitoring = false

    /**
     * Start monitoring remote controller button presses
     */
    fun startMonitoring() {
        if (isMonitoring) {
            LogUtils.w(TAG, "Already monitoring RC buttons")
            return
        }

        LogUtils.i(TAG, "Starting RC button monitoring...")
        
        // Monitor shutter button (picture button)
        KeyManager.getInstance().listen(
            KeyCameraShutterCommand.create(),
            this,
            object : CommonCallbacks.KeyListener<CameraShutterCommand> {
                override fun onValueChange(oldValue: CameraShutterCommand?, newValue: CameraShutterCommand?) {
                    if (newValue == CameraShutterCommand.START_TAKE_PHOTO) {
                        LogUtils.i(TAG, "Shutter button pressed - triggering capture")
                        _shutterButtonPressed.postValue(true)
                    } else {
                        _shutterButtonPressed.postValue(false)
                    }
                }
            }
        )
        
        // Monitor record button (optional)
        KeyManager.getInstance().listen(
            KeyCameraRecording.create(),
            this,
            object : CommonCallbacks.KeyListener<CameraRecordingState> {
                override fun onValueChange(oldValue: CameraRecordingState?, newValue: CameraRecordingState?) {
                    val isRecording = newValue == CameraRecordingState.STARTING || newValue == CameraRecordingState.RECORDING
                    _recordButtonPressed.postValue(isRecording)
                    LogUtils.d(TAG, "Record button state: $newValue")
                }
            }
        )
        
        isMonitoring = true
        LogUtils.i(TAG, "RC button monitoring started")
    }

    /**
     * Stop monitoring remote controller buttons
     */
    fun stopMonitoring() {
        if (!isMonitoring) {
            return
        }

        LogUtils.i(TAG, "Stopping RC button monitoring...")
        
        KeyManager.getInstance().cancelListen(this)
        
        isMonitoring = false
        LogUtils.i(TAG, "RC button monitoring stopped")
    }

    /**
     * Check if currently monitoring
     */
    fun isMonitoring(): Boolean = isMonitoring
}
