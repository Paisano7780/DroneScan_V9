package com.dronescan.msdksample.dji

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dji.sdk.keyvalue.key.RemoteControllerKey
import dji.v5.et.create
import dji.v5.manager.KeyManager
import dji.v5.common.callback.CommonCallbacks
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
            RemoteControllerKey.KeyShutterButtonDown.create(),
            this,
            object : CommonCallbacks.KeyListener<Boolean> {
                override fun onValueChange(oldValue: Boolean?, newValue: Boolean?) {
                    val pressed = newValue ?: false
                    LogUtils.d(TAG, "Shutter button: $pressed")
                    _shutterButtonPressed.postValue(pressed)
                    
                    // Trigger on button press (not release)
                    if (pressed) {
                        LogUtils.i(TAG, "Shutter button pressed - triggering capture")
                    }
                }
            }
        )
        
        // Monitor record button (optional)
        KeyManager.getInstance().listen(
            RemoteControllerKey.KeyRecordButtonDown.create(),
            this,
            object : CommonCallbacks.KeyListener<Boolean> {
                override fun onValueChange(oldValue: Boolean?, newValue: Boolean?) {
                    val pressed = newValue ?: false
                    LogUtils.d(TAG, "Record button: $pressed")
                    _recordButtonPressed.postValue(pressed)
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
