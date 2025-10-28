package com.dronescan.msdksample.utils

import android.util.Log

/**
 * Logging utility for the application
 */
object LogUtils {
    private const val APP_TAG = "DroneScan"
    private var isDebugMode = true

    fun setDebugMode(debug: Boolean) {
        isDebugMode = debug
    }

    fun d(tag: String, message: String) {
        if (isDebugMode) {
            Log.d("$APP_TAG:$tag", message)
        }
    }

    fun i(tag: String, message: String) {
        Log.i("$APP_TAG:$tag", message)
    }

    fun w(tag: String, message: String) {
        Log.w("$APP_TAG:$tag", message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e("$APP_TAG:$tag", message, throwable)
        } else {
            Log.e("$APP_TAG:$tag", message)
        }
    }
}
