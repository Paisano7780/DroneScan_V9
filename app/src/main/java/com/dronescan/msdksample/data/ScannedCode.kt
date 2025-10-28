package com.dronescan.msdksample.data

import android.location.Location
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data model for a scanned barcode/QR code
 */
data class ScannedCode(
    @SerializedName("id")
    val id: String = UUID.randomUUID().toString(),
    
    @SerializedName("value")
    val value: String,
    
    @SerializedName("format")
    val format: String,
    
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @SerializedName("latitude")
    val latitude: Double? = null,
    
    @SerializedName("longitude")
    val longitude: Double? = null,
    
    @SerializedName("altitude")
    val altitude: Double? = null,
    
    @SerializedName("accuracy")
    val accuracy: Float? = null,
    
    @SerializedName("notes")
    var notes: String? = null
) {
    /**
     * Get formatted timestamp
     */
    fun getFormattedTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Get formatted location
     */
    fun getFormattedLocation(): String {
        return if (latitude != null && longitude != null) {
            "Lat: %.6f, Lon: %.6f".format(latitude, longitude)
        } else {
            "No location"
        }
    }

    /**
     * Check if has valid GPS location
     */
    fun hasLocation(): Boolean = latitude != null && longitude != null

    /**
     * Convert to CSV row
     */
    fun toCsvRow(): String {
        return listOf(
            id,
            value,
            format,
            getFormattedTimestamp(),
            latitude?.toString() ?: "",
            longitude?.toString() ?: "",
            altitude?.toString() ?: "",
            accuracy?.toString() ?: "",
            notes ?: ""
        ).joinToString(",") { "\"$it\"" }
    }

    companion object {
        /**
         * CSV header
         */
        fun getCsvHeader(): String {
            return "ID,Value,Format,Timestamp,Latitude,Longitude,Altitude,Accuracy,Notes"
        }

        /**
         * Create from location
         */
        fun create(
            value: String,
            format: String,
            location: Location?,
            notes: String? = null
        ): ScannedCode {
            return ScannedCode(
                value = value,
                format = format,
                latitude = location?.latitude,
                longitude = location?.longitude,
                altitude = location?.altitude,
                accuracy = location?.accuracy,
                notes = notes
            )
        }
    }
}

/**
 * Data class for scan session
 */
data class ScanSession(
    @SerializedName("session_id")
    val sessionId: String = UUID.randomUUID().toString(),
    
    @SerializedName("start_time")
    val startTime: Long = System.currentTimeMillis(),
    
    @SerializedName("end_time")
    var endTime: Long? = null,
    
    @SerializedName("scanned_codes")
    val scannedCodes: MutableList<ScannedCode> = mutableListOf(),
    
    @SerializedName("notes")
    var notes: String? = null
) {
    /**
     * Add scanned code to session
     */
    fun addScannedCode(code: ScannedCode) {
        scannedCodes.add(code)
    }

    /**
     * Close session
     */
    fun close() {
        endTime = System.currentTimeMillis()
    }

    /**
     * Get session duration in seconds
     */
    fun getDurationSeconds(): Long {
        val end = endTime ?: System.currentTimeMillis()
        return (end - startTime) / 1000
    }

    /**
     * Get formatted session info
     */
    fun getFormattedInfo(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val startStr = sdf.format(Date(startTime))
        val duration = getDurationSeconds()
        return "Session: $sessionId\nStart: $startStr\nCodes: ${scannedCodes.size}\nDuration: ${duration}s"
    }
}
