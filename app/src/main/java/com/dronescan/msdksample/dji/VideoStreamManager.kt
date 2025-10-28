package com.dronescan.msdksample.dji

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.view.Surface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dji.sdk.keyvalue.key.CameraKey
import dji.sdk.keyvalue.value.common.CameraLensType
import dji.sdk.keyvalue.value.common.ComponentIndexType
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.common.video.channel.VideoChannelType
import dji.v5.common.video.decoder.DecoderOutputMode
import dji.v5.common.video.decoder.VideoDecoder
import dji.v5.common.video.interfaces.IVideoDecoder
import dji.v5.et.create
import dji.v5.manager.KeyManager
import dji.v5.manager.datacenter.MediaDataCenter
import dji.v5.manager.interfaces.ICameraStreamManager
import dji.sdk.keyvalue.value.common.EmptyMsg
import com.dronescan.msdksample.utils.LogUtils
import java.io.ByteArrayOutputStream

/**
 * Manages video stream from DJI drone camera
 * Provides decoded video frames for barcode detection
 */
class VideoStreamManager(
    private val cameraIndex: ComponentIndexType = ComponentIndexType.LEFT_OR_MAIN,
    private val lensType: CameraLensType = CameraLensType.CAMERA_LENS_ZOOM
) {

    companion object {
        private const val TAG = "VideoStreamManager"
    }

    private val cameraStreamManager: ICameraStreamManager = MediaDataCenter.getInstance().cameraStreamManager
    private var videoDecoder: IVideoDecoder? = null
    private var currentSurface: Surface? = null  // Track current surface
    
    // LiveData for video stream state
    private val _isStreaming = MutableLiveData<Boolean>(false)
    val isStreaming: LiveData<Boolean> = _isStreaming
    
    // Callback for decoded frames
    private var frameCallback: ((Bitmap) -> Unit)? = null
    private var yuvFrameCallback: ((ByteArray, Int, Int) -> Unit)? = null

    /**
     * Start video stream
     */
    fun startStream(surface: Surface, width: Int, height: Int) {
        LogUtils.i(TAG, "Starting video stream...")
        
        try {
            // Put camera stream surface  
            // Signature: putCameraStreamSurface(surface: Surface, scaleType: ScaleType)
            cameraStreamManager.putCameraStreamSurface(
                ComponentIndexType.LEFT_OR_MAIN,
                surface,
                width,
                height,
                ICameraStreamManager.ScaleType.CENTER_INSIDE
            )
            
            currentSurface = surface  // Store for later removal
            _isStreaming.postValue(true)
            LogUtils.i(TAG, "Video stream started successfully")
        } catch (e: Exception) {
            LogUtils.e(TAG, "Failed to start video stream", e)
            _isStreaming.postValue(false)
        }
    }

    /**
     * Start video decoder for frame processing
     */
    fun startDecoder(surface: Surface?, width: Int, height: Int) {
        LogUtils.i(TAG, "Starting video decoder...")
        
        videoDecoder?.destroy()
        
        videoDecoder = VideoDecoder(
            null, // context
            VideoChannelType.PRIMARY_STREAM_CHANNEL,
            DecoderOutputMode.SURFACE_MODE, // Cambiar a SURFACE_MODE por compatibilidad
            surface,
            width,
            height,
            false
        )
        
        // Note: setYuvDataListener is not available in this SDK version
        // Alternative: Use frame listener through stream manager
        
        videoDecoder?.onResume()
        LogUtils.i(TAG, "Video decoder started")
    }

    private fun handleYuvFrame(data: ByteArray, width: Int, height: Int, format: Int) {
        try {
            // Notify YUV callback
            yuvFrameCallback?.invoke(data, width, height)
            
            // Convert to bitmap if bitmap callback is set
            frameCallback?.let { callback ->
                val bitmap = convertYuvToBitmap(data, width, height)
                bitmap?.let { callback(it) }
            }
        } catch (e: Exception) {
            LogUtils.e(TAG, "Error handling YUV frame", e)
        }
    }

    private fun convertYuvToBitmap(yuvData: ByteArray, width: Int, height: Int): Bitmap? {
        return try {
            val yuvImage = YuvImage(yuvData, ImageFormat.NV21, width, height, null)
            val outputStream = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, width, height), 75, outputStream)
            val jpegData = outputStream.toByteArray()
            android.graphics.BitmapFactory.decodeByteArray(jpegData, 0, jpegData.size)
        } catch (e: Exception) {
            LogUtils.e(TAG, "Error converting YUV to Bitmap", e)
            null
        }
    }

    /**
     * Set frame callback for barcode detection
     */
    fun setFrameCallback(callback: (Bitmap) -> Unit) {
        this.frameCallback = callback
    }

    /**
     * Set YUV frame callback for direct YUV processing
     */
    fun setYuvFrameCallback(callback: (ByteArray, Int, Int) -> Unit) {
        this.yuvFrameCallback = callback
    }

    /**
     * Stop video stream
     */
    fun stopStream() {
        LogUtils.i(TAG, "Stopping video stream...")
        
        try {
            videoDecoder?.onPause()
            videoDecoder?.destroy()
            videoDecoder = null
            
            // Remove camera stream surface if we have one
            currentSurface?.let { surface ->
                cameraStreamManager.removeCameraStreamSurface(surface)
                currentSurface = null
            }
            
            _isStreaming.postValue(false)
            LogUtils.i(TAG, "Video stream stopped")
        } catch (e: Exception) {
            LogUtils.e(TAG, "Error stopping video stream", e)
        }
    }

    /**
     * Take a photo using camera
     */
    fun takePhoto(callback: (Boolean, String?) -> Unit) {
        LogUtils.d(TAG, "Taking photo...")
        
        KeyManager.getInstance().performAction(
            CameraKey.KeyStartShootPhoto.create(cameraIndex),
            object : CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
                override fun onSuccess(msg: EmptyMsg?) {
                    LogUtils.i(TAG, "Photo captured successfully")
                    callback(true, null)
                }

                override fun onFailure(error: IDJIError) {
                    LogUtils.e(TAG, "Failed to capture photo: ${error.description()}")
                    callback(false, error.description())
                }
            }
        )
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        stopStream()
        frameCallback = null
        yuvFrameCallback = null
    }
}
