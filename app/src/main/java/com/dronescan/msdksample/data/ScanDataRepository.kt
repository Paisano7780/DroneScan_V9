package com.dronescan.msdksample.data

import android.content.Context
import android.location.Location
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.dronescan.msdksample.utils.LogUtils
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository for managing scanned codes data
 * Handles saving to JSON and CSV formats
 */
class ScanDataRepository(private val context: Context) {

    companion object {
        private const val TAG = "ScanDataRepository"
        private const val FOLDER_NAME = "DroneScan"
        private const val JSON_FILE_PREFIX = "scan_session"
        private const val CSV_FILE_PREFIX = "scan_data"
    }

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private var currentSession: ScanSession? = null

    /**
     * Start a new scan session
     */
    fun startSession(notes: String? = null): ScanSession {
        val session = ScanSession(notes = notes)
        currentSession = session
        LogUtils.i(TAG, "Started new scan session: ${session.sessionId}")
        return session
    }

    /**
     * Add scanned code to current session
     */
    fun addScannedCode(
        value: String,
        format: String,
        location: Location?,
        notes: String? = null
    ): ScannedCode? {
        val session = currentSession
        if (session == null) {
            LogUtils.w(TAG, "No active session, creating new one")
            startSession()
        }

        val code = ScannedCode.create(value, format, location, notes)
        currentSession?.addScannedCode(code)
        
        LogUtils.d(TAG, "Added scanned code: $value ($format)")
        return code
    }

    /**
     * Get current session
     */
    fun getCurrentSession(): ScanSession? = currentSession

    /**
     * Close current session and save data
     */
    fun closeSession(): File? {
        val session = currentSession ?: return null
        session.close()
        
        val jsonFile = saveSessionToJson(session)
        val csvFile = saveSessionToCsv(session)
        
        LogUtils.i(TAG, "Session closed: ${session.sessionId}, ${session.scannedCodes.size} codes")
        currentSession = null
        
        return jsonFile
    }

    /**
     * Save session to JSON file
     */
    private fun saveSessionToJson(session: ScanSession): File? {
        return try {
            val folder = getStorageFolder()
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "${JSON_FILE_PREFIX}_${timestamp}.json"
            val file = File(folder, fileName)
            
            val json = gson.toJson(session)
            FileWriter(file).use { writer ->
                writer.write(json)
            }
            
            LogUtils.i(TAG, "Saved session to JSON: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            LogUtils.e(TAG, "Failed to save session to JSON", e)
            null
        }
    }

    /**
     * Save session to CSV file
     */
    private fun saveSessionToCsv(session: ScanSession): File? {
        return try {
            val folder = getStorageFolder()
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "${CSV_FILE_PREFIX}_${timestamp}.csv"
            val file = File(folder, fileName)
            
            FileWriter(file).use { writer ->
                // Write header
                writer.write(ScannedCode.getCsvHeader() + "\n")
                
                // Write data rows
                session.scannedCodes.forEach { code ->
                    writer.write(code.toCsvRow() + "\n")
                }
            }
            
            LogUtils.i(TAG, "Saved session to CSV: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            LogUtils.e(TAG, "Failed to save session to CSV", e)
            null
        }
    }

    /**
     * Get or create storage folder
     */
    private fun getStorageFolder(): File {
        val folder = File(context.getExternalFilesDir(null), FOLDER_NAME)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return folder
    }

    /**
     * Get all saved session files
     */
    fun getSavedSessions(): List<File> {
        val folder = getStorageFolder()
        return folder.listFiles { file ->
            file.name.startsWith(JSON_FILE_PREFIX) && file.name.endsWith(".json")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    /**
     * Load session from file
     */
    fun loadSession(file: File): ScanSession? {
        return try {
            val json = file.readText()
            gson.fromJson(json, ScanSession::class.java)
        } catch (e: Exception) {
            LogUtils.e(TAG, "Failed to load session from file", e)
            null
        }
    }

    /**
     * Export current session immediately (without closing)
     */
    fun exportCurrentSession(): Pair<File?, File?> {
        val session = currentSession ?: return Pair(null, null)
        val jsonFile = saveSessionToJson(session)
        val csvFile = saveSessionToCsv(session)
        return Pair(jsonFile, csvFile)
    }

    /**
     * Clear all data
     */
    fun clearAllData() {
        currentSession = null
        val folder = getStorageFolder()
        folder.listFiles()?.forEach { it.delete() }
        LogUtils.i(TAG, "Cleared all scan data")
    }
}
