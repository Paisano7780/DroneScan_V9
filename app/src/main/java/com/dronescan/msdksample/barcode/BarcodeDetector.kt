package com.dronescan.msdksample.barcode

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.dronescan.msdksample.utils.LogUtils
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Barcode/QR Code detector using ML Kit
 * Processes video frames to detect barcodes in real-time
 */
class BarcodeDetector {

    companion object {
        private const val TAG = "BarcodeDetector"
    }

    private val scanner = BarcodeScanning.getClient()
    private val isProcessing = AtomicBoolean(false)
    
    // LiveData for detected barcodes
    private val _detectedCodes = MutableLiveData<List<DetectedCode>>()
    val detectedCodes: LiveData<List<DetectedCode>> = _detectedCodes
    
    // LiveData for detection state
    private val _isDetecting = MutableLiveData<Boolean>(false)
    val isDetecting: LiveData<Boolean> = _isDetecting
    
    private var isEnabled = true

    /**
     * Process a bitmap frame to detect barcodes
     */
    fun processFrame(bitmap: Bitmap) {
        if (!isEnabled || !isProcessing.compareAndSet(false, true)) {
            return
        }

        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    handleDetectedBarcodes(barcodes)
                }
                .addOnFailureListener { exception ->
                    LogUtils.e(TAG, "Barcode detection failed", exception)
                }
                .addOnCompleteListener {
                    isProcessing.set(false)
                }
        } catch (e: Exception) {
            LogUtils.e(TAG, "Error processing frame", e)
            isProcessing.set(false)
        }
    }

    /**
     * Process YUV image data from DJI camera
     */
    fun processYuvFrame(yuvData: ByteArray, width: Int, height: Int, rotation: Int) {
        if (!isEnabled || !isProcessing.compareAndSet(false, true)) {
            return
        }

        try {
            val image = InputImage.fromByteArray(
                yuvData,
                width,
                height,
                rotation,
                InputImage.IMAGE_FORMAT_NV21
            )
            
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    handleDetectedBarcodes(barcodes)
                }
                .addOnFailureListener { exception ->
                    LogUtils.e(TAG, "YUV barcode detection failed", exception)
                }
                .addOnCompleteListener {
                    isProcessing.set(false)
                }
        } catch (e: Exception) {
            LogUtils.e(TAG, "Error processing YUV frame", e)
            isProcessing.set(false)
        }
    }

    private fun handleDetectedBarcodes(barcodes: List<Barcode>) {
        if (barcodes.isEmpty()) {
            _detectedCodes.postValue(emptyList())
            _isDetecting.postValue(false)
            return
        }

        val detectedList = barcodes.mapNotNull { barcode ->
            barcode.rawValue?.let { value ->
                DetectedCode(
                    value = value,
                    format = getBarcodeFormatName(barcode.format),
                    boundingBox = barcode.boundingBox,
                    cornerPoints = barcode.cornerPoints
                )
            }
        }

        _detectedCodes.postValue(detectedList)
        _isDetecting.postValue(detectedList.isNotEmpty())
        
        LogUtils.d(TAG, "Detected ${detectedList.size} code(s)")
    }

    private fun getBarcodeFormatName(format: Int): String {
        return when (format) {
            Barcode.FORMAT_QR_CODE -> "QR_CODE"
            Barcode.FORMAT_CODE_128 -> "CODE_128"
            Barcode.FORMAT_CODE_39 -> "CODE_39"
            Barcode.FORMAT_CODE_93 -> "CODE_93"
            Barcode.FORMAT_CODABAR -> "CODABAR"
            Barcode.FORMAT_DATA_MATRIX -> "DATA_MATRIX"
            Barcode.FORMAT_EAN_13 -> "EAN_13"
            Barcode.FORMAT_EAN_8 -> "EAN_8"
            Barcode.FORMAT_ITF -> "ITF"
            Barcode.FORMAT_UPC_A -> "UPC_A"
            Barcode.FORMAT_UPC_E -> "UPC_E"
            Barcode.FORMAT_PDF417 -> "PDF417"
            Barcode.FORMAT_AZTEC -> "AZTEC"
            else -> "UNKNOWN"
        }
    }

    fun enable() {
        isEnabled = true
        LogUtils.d(TAG, "Barcode detection enabled")
    }

    fun disable() {
        isEnabled = false
        _detectedCodes.postValue(emptyList())
        _isDetecting.postValue(false)
        LogUtils.d(TAG, "Barcode detection disabled")
    }

    fun cleanup() {
        scanner.close()
    }
}

/**
 * Data class representing a detected code
 */
data class DetectedCode(
    val value: String,
    val format: String,
    val boundingBox: android.graphics.Rect?,
    val cornerPoints: Array<android.graphics.Point>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DetectedCode

        if (value != other.value) return false
        if (format != other.format) return false
        if (boundingBox != other.boundingBox) return false
        if (cornerPoints != null) {
            if (other.cornerPoints == null) return false
            if (!cornerPoints.contentEquals(other.cornerPoints)) return false
        } else if (other.cornerPoints != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + format.hashCode()
        result = 31 * result + (boundingBox?.hashCode() ?: 0)
        result = 31 * result + (cornerPoints?.contentHashCode() ?: 0)
        return result
    }
}
