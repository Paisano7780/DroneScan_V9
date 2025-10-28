package com.dronescan.msdksample.dji

import android.content.Context
import androidx.lifecycle.MutableLiveData
import dji.v5.common.error.IDJIError
import dji.v5.common.register.DJISDKInitEvent
import dji.v5.manager.SDKManager
import dji.v5.manager.interfaces.SDKManagerCallback
import dji.v5.network.DJINetworkManager
import com.dronescan.msdksample.utils.LogUtils

/**
 * Helper class to initialize and manage DJI Mobile SDK V5
 */
class DJISDKHelper private constructor() {

    companion object {
        private const val TAG = "DJISDKHelper"
        
        @Volatile
        private var instance: DJISDKHelper? = null

        fun getInstance(): DJISDKHelper {
            return instance ?: synchronized(this) {
                instance ?: DJISDKHelper().also { instance = it }
            }
        }
    }

    // LiveData for observing SDK state
    val registrationState = MutableLiveData<Pair<Boolean, IDJIError?>>()
    val productConnectionState = MutableLiveData<Pair<Boolean, Int>>()
    val initializationProgress = MutableLiveData<Pair<DJISDKInitEvent, Int>>()
    val databaseDownloadProgress = MutableLiveData<Pair<Long, Long>>()
    
    private var isInitialized = false
    private var isRegistered = false

    /**
     * Initialize DJI Mobile SDK
     */
    fun init(context: Context) {
        LogUtils.i(TAG, "Initializing DJI Mobile SDK...")
        
        SDKManager.getInstance().init(context, object : SDKManagerCallback {
            override fun onRegisterSuccess() {
                LogUtils.i(TAG, "DJI SDK Registration Success")
                isRegistered = true
                registrationState.postValue(Pair(true, null))
            }

            override fun onRegisterFailure(error: IDJIError) {
                LogUtils.e(TAG, "DJI SDK Registration Failed: ${error.description()}")
                isRegistered = false
                registrationState.postValue(Pair(false, error))
            }

            override fun onProductDisconnect(productId: Int) {
                LogUtils.i(TAG, "Product Disconnected: $productId")
                productConnectionState.postValue(Pair(false, productId))
            }

            override fun onProductConnect(productId: Int) {
                LogUtils.i(TAG, "Product Connected: $productId")
                productConnectionState.postValue(Pair(true, productId))
            }

            override fun onProductChanged(productId: Int) {
                LogUtils.i(TAG, "Product Changed: $productId")
            }

            override fun onInitProcess(event: DJISDKInitEvent, totalProcess: Int) {
                LogUtils.d(TAG, "Init Process: $event, Progress: $totalProcess")
                initializationProgress.postValue(Pair(event, totalProcess))
                
                if (event == DJISDKInitEvent.INITIALIZE_COMPLETE) {
                    isInitialized = true
                    LogUtils.i(TAG, "SDK Initialization Complete")
                    SDKManager.getInstance().registerApp()
                }
            }

            override fun onDatabaseDownloadProgress(current: Long, total: Long) {
                databaseDownloadProgress.postValue(Pair(current, total))
                LogUtils.d(TAG, "Database Download: $current / $total")
            }
        })
        
        // Monitor network status for auto-registration
        DJINetworkManager.getInstance().addNetworkStatusListener { isAvailable ->
            if (isInitialized && isAvailable && !isRegistered) {
                LogUtils.d(TAG, "Network available, attempting registration...")
                SDKManager.getInstance().registerApp()
            }
        }
    }

    /**
     * Check if SDK is registered
     */
    fun isSDKRegistered(): Boolean {
        return SDKManager.getInstance().isRegistered
    }

    /**
     * Destroy SDK (call on app exit)
     */
    fun destroy() {
        SDKManager.getInstance().destroy()
        isInitialized = false
        isRegistered = false
    }
}
