package com.dronescan.msdksample

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.dronescan.msdksample.dji.DJISDKHelper
import com.dronescan.msdksample.utils.LogUtils

/**
 * DroneScan Application Class
 * Initializes DJI Mobile SDK and other application-wide components
 */
class DroneScanApplication : Application() {

    companion object {
        private const val TAG = "DroneScanApp"
        lateinit var instance: DroneScanApplication
            private set
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        LogUtils.d(TAG, "DroneScan Application Starting...")
        
        // Initialize DJI SDK
        DJISDKHelper.getInstance().init(this)
        
        LogUtils.d(TAG, "DroneScan Application Initialized")
    }
}
