package com.dronescan.msdksample.dji

import androidx.lifecycle.MutableLiveData
import dji.sdk.keyvalue.value.common.LocationCoordinate2D
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.aircraft.simulator.InitializationSettings
import dji.v5.manager.aircraft.simulator.SimulatorManager as DJISimulatorManager
import dji.v5.manager.aircraft.simulator.SimulatorState
import dji.v5.manager.aircraft.simulator.SimulatorStatusListener
import com.dronescan.msdksample.utils.LogUtils

/**
 * Manager for DJI Aircraft Simulator
 * Allows testing drone functionality without physical hardware
 */
class SimulatorManager private constructor() {

    companion object {
        private const val TAG = "SimulatorManager"
        
        // Default test location: Shenzhen, China (DJI HQ)
        private const val DEFAULT_LATITUDE = 22.5431
        private const val DEFAULT_LONGITUDE = 114.0579
        private const val DEFAULT_SATELLITE_COUNT = 12
        private const val DEFAULT_UPDATE_FREQUENCY = 10 // Hz
        
        @Volatile
        private var instance: SimulatorManager? = null

        fun getInstance(): SimulatorManager {
            return instance ?: synchronized(this) {
                instance ?: SimulatorManager().also { instance = it }
            }
        }
    }

    // LiveData for simulator state
    val simulatorState = MutableLiveData<SimulatorState>()
    val isSimulatorActive = MutableLiveData<Boolean>(false)
    val simulatorError = MutableLiveData<IDJIError?>()

    private var simulatorStatusListener: SimulatorStatusListener? = null

    /**
     * Start simulator with default location
     */
    fun startSimulator(callback: CommonCallbacks.CompletionCallback? = null) {
        startSimulator(
            latitude = DEFAULT_LATITUDE,
            longitude = DEFAULT_LONGITUDE,
            satelliteCount = DEFAULT_SATELLITE_COUNT,
            callback = callback
        )
    }

    /**
     * Start simulator with custom location
     * @param latitude Initial latitude (default: Shenzhen, China)
     * @param longitude Initial longitude (default: Shenzhen, China)
     * @param satelliteCount Number of GPS satellites (affects GPS quality)
     * @param updateFrequency Update frequency in Hz (default: 10Hz)
     * @param callback Completion callback
     */
    fun startSimulator(
        latitude: Double = DEFAULT_LATITUDE,
        longitude: Double = DEFAULT_LONGITUDE,
        satelliteCount: Int = DEFAULT_SATELLITE_COUNT,
        updateFrequency: Int = DEFAULT_UPDATE_FREQUENCY,
        callback: CommonCallbacks.CompletionCallback? = null
    ) {
        LogUtils.i(TAG, "Starting simulator at ($latitude, $longitude) with $satelliteCount satellites @ ${updateFrequency}Hz")
        
        // Check if simulator is already running
        if (isSimulatorRunning()) {
            LogUtils.w(TAG, "⚠️ Simulator already running, stopping first...")
            stopSimulator(object : CommonCallbacks.CompletionCallback {
                override fun onSuccess() {
                    // Wait a bit before restarting
                    Thread.sleep(500)
                    startSimulatorInternal(latitude, longitude, satelliteCount, updateFrequency, callback)
                }
                override fun onFailure(error: IDJIError) {
                    LogUtils.e(TAG, "Failed to stop existing simulator: ${error.description()}")
                    callback?.onFailure(error)
                }
            })
            return
        }
        
        startSimulatorInternal(latitude, longitude, satelliteCount, updateFrequency, callback)
    }
    
    private fun startSimulatorInternal(
        latitude: Double,
        longitude: Double,
        satelliteCount: Int,
        updateFrequency: Int,
        callback: CommonCallbacks.CompletionCallback?
    ) {
        // Note: UpdateFrequency is ignored as createInstance only accepts location and satelliteCount
        val settings = InitializationSettings.createInstance(
            LocationCoordinate2D(latitude, longitude),
            satelliteCount
        )

        DJISimulatorManager.getInstance().enableSimulator(settings, object : CommonCallbacks.CompletionCallback {
            override fun onSuccess() {
                LogUtils.i(TAG, "✅ Simulator started successfully")
                isSimulatorActive.postValue(true)
                simulatorError.postValue(null)
                startListeningToSimulatorState()
                callback?.onSuccess()
            }

            override fun onFailure(error: IDJIError) {
                LogUtils.e(TAG, "❌ Failed to start simulator: ${error.description()}")
                isSimulatorActive.postValue(false)
                simulatorError.postValue(error)
                callback?.onFailure(error)
            }
        })
    }

    /**
     * Stop the simulator
     */
    fun stopSimulator(callback: CommonCallbacks.CompletionCallback? = null) {
        LogUtils.i(TAG, "Stopping simulator...")
        
        DJISimulatorManager.getInstance().disableSimulator(object : CommonCallbacks.CompletionCallback {
            override fun onSuccess() {
                LogUtils.i(TAG, "✅ Simulator stopped successfully")
                isSimulatorActive.postValue(false)
                stopListeningToSimulatorState()
                callback?.onSuccess()
            }

            override fun onFailure(error: IDJIError) {
                LogUtils.e(TAG, "❌ Failed to stop simulator: ${error.description()}")
                simulatorError.postValue(error)
                callback?.onFailure(error)
            }
        })
    }

    /**
     * Check if simulator is currently running
     */
    fun isSimulatorRunning(): Boolean {
        return DJISimulatorManager.getInstance().isSimulatorEnabled
    }

    /**
     * Start listening to simulator state updates
     */
    private fun startListeningToSimulatorState() {
        if (simulatorStatusListener == null) {
            simulatorStatusListener = SimulatorStatusListener { state ->
                // Use API names from MSDK v5 sample: positionX/Y/Z and location.latitude/longitude
                LogUtils.d(TAG, "Simulator State: Lat=${state.location.latitude}, " +
                        "Lon=${state.location.longitude}, Alt=${state.positionZ}, " +
                        "IsFlying=${state.areMotorsOn()}")
                simulatorState.postValue(state)
            }
            DJISimulatorManager.getInstance().addSimulatorStateListener(simulatorStatusListener)
        }
    }

    /**
     * Stop listening to simulator state updates
     */
    private fun stopListeningToSimulatorState() {
        simulatorStatusListener?.let {
            DJISimulatorManager.getInstance().removeSimulatorStateListener(it)
            simulatorStatusListener = null
        }
    }

    /**
     * Simulate drone movement
     * @param latitude Target latitude
     * @param longitude Target longitude
     * @param altitude Target altitude in meters
     */
    fun flyTo(
        latitude: Double,
        longitude: Double,
        altitude: Double,
        callback: CommonCallbacks.CompletionCallback? = null
    ) {
        LogUtils.i(TAG, "Flying to: ($latitude, $longitude) at ${altitude}m")
        // This would use VirtualStick or Waypoint mission in real implementation
        callback?.onSuccess()
    }

    /**
     * Clean up resources
     */
    fun cleanup() {
        stopListeningToSimulatorState()
    }
}
