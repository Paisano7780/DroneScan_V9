package com.dronescan.msdksample

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.dronescan.msdksample.dji.DJISDKHelper
import com.dronescan.msdksample.dji.SimulatorManager
import com.dronescan.msdksample.utils.LogUtils
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError

/**
 * DroneScan Application Class
 * Initializes DJI Mobile SDK and other application-wide components
 */
class DroneScanApplication : Application() {

    companion object {
        private const val TAG = "DroneScanApp"
        lateinit var instance: DroneScanApplication
            private set
        
        /**
         * Enable test mode to automatically start simulator
         * Set to true in instrumented tests or via build config
         */
        var isTestMode: Boolean = false
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        LogUtils.d(TAG, "DroneScan Application Starting...")
        
        // Detect if running in test environment
        isTestMode = isRunningInTestEnvironment()
        if (isTestMode) {
            LogUtils.i(TAG, "🧪 TEST MODE DETECTED - Simulator will auto-start")
        }
        
        // Initialize DJI SDK with error handling for test environments
        try {
            DJISDKHelper.getInstance().init(this)
            
            // Auto-start simulator in test mode after SDK registration
            if (isTestMode) {
                DJISDKHelper.getInstance().registrationState.observeForever { (success, error) ->
                    if (success) {
                        LogUtils.i(TAG, "SDK registered, starting simulator...")
                        startSimulatorForTesting()
                    }
                }
            }
        } catch (e: NoClassDefFoundError) {
            // DJI SDK classes not available in test APK - this is expected in some test scenarios
            LogUtils.w(TAG, "⚠️ DJI SDK classes not available: ${e.message}")
            LogUtils.i(TAG, "Running in limited test mode without DJI SDK")
        } catch (e: Exception) {
            LogUtils.e(TAG, "❌ Failed to initialize DJI SDK: ${e.message}", e)
        }
        
        LogUtils.d(TAG, "DroneScan Application Initialized")
    }
    
    /**
     * Detect if app is running in test environment
     */
    private fun isRunningInTestEnvironment(): Boolean {
        return try {
            // Check for Android Test framework
            Class.forName("androidx.test.espresso.Espresso")
            true
        } catch (e: ClassNotFoundException) {
            // Check for Firebase Test Lab
            android.os.Build.FINGERPRINT.contains("generic") &&
            android.os.Build.MODEL.contains("sdk")
        }
    }
    
    /**
     * Start simulator for automated testing
     */
    private fun startSimulatorForTesting() {
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            SimulatorManager.getInstance().startSimulator(object : CommonCallbacks.CompletionCallback {
                override fun onSuccess() {
                    LogUtils.i(TAG, "✅ Test simulator started successfully")
                }

                override fun onFailure(error: IDJIError) {
                    LogUtils.e(TAG, "❌ Failed to start test simulator: ${error.description()}")
                }
            })
        }, 5000) // Wait 5 seconds for SDK to fully initialize
    }
}
