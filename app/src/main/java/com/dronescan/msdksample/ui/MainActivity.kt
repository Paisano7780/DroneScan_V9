package com.dronescan.msdksample.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.dronescan.msdksample.DroneScanApplication
import com.dronescan.msdksample.R
import com.dronescan.msdksample.databinding.ActivityMainBinding
import com.dronescan.msdksample.dji.DJISDKHelper
import com.dronescan.msdksample.utils.LogUtils
import com.dronescan.msdksample.viewmodel.ScanState
import com.dronescan.msdksample.viewmodel.ScanViewModel

/**
 * Main Activity for DroneScan application
 * Displays video feed, barcode detection overlay, and scan controls
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
    }

    private lateinit var binding: ActivityMainBinding
    private var viewModel: ScanViewModel? = null
    private val djiHelper = DJISDKHelper.getInstance()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            LogUtils.i(TAG, "All permissions granted")
            setupApplication()
        } else {
            showPermissionDeniedDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LogUtils.i(TAG, "MainActivity created")
        
        // Only initialize ViewModel if DJI SDK is available (not in limited test mode)
        if (!DroneScanApplication.isTestMode || isDJISDKAvailable()) {
            try {
                val vm: ScanViewModel by viewModels()
                viewModel = vm
                setupObservers()
            } catch (e: Exception) {
                LogUtils.w(TAG, "Failed to initialize ViewModel: ${e.message}")
                // Continue without ViewModel in test mode
            }
        } else {
            LogUtils.i(TAG, "Running in limited test mode - ViewModel not initialized")
        }
        
        setupButtons()
        checkPermissions()
    }
    
    private fun isDJISDKAvailable(): Boolean {
        return try {
            // Try to access a DJI SDK class
            Class.forName("dji.v5.manager.SDKManager")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    private fun checkPermissions() {
        val missingPermissions = REQUIRED_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            setupApplication()
        } else {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    private fun setupApplication() {
        setupVideoSurface()
        observeSDKState()
    }

    private fun setupVideoSurface() {
        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                LogUtils.d(TAG, "Surface created")
                val width = binding.surfaceView.width
                val height = binding.surfaceView.height
                viewModel?.setVideoStreamCallback(holder.surface, width, height)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                LogUtils.d(TAG, "Surface changed: ${width}x${height}")
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                LogUtils.d(TAG, "Surface destroyed")
            }
        })
    }

    private fun observeSDKState() {
        djiHelper.registrationState.observe(this) { (success, error) ->
            if (success) {
                binding.tvSdkStatus.text = "SDK: Registered ✓"
                binding.tvSdkStatus.setTextColor(getColor(R.color.status_ok))
            } else {
                binding.tvSdkStatus.text = "SDK: Error - ${error?.description() ?: "Unknown"}"
                binding.tvSdkStatus.setTextColor(getColor(R.color.status_error))
            }
        }

        djiHelper.productConnectionState.observe(this) { (connected, _) ->
            if (connected) {
                binding.tvDroneStatus.text = "Drone: Connected ✓"
                binding.tvDroneStatus.setTextColor(getColor(R.color.status_ok))
                enableControls(true)
            } else {
                binding.tvDroneStatus.text = "Drone: Disconnected"
                binding.tvDroneStatus.setTextColor(getColor(R.color.status_warning))
                enableControls(false)
            }
        }

        djiHelper.initializationProgress.observe(this) { (_, progress) ->
            binding.progressBar.visibility = if (progress < 100) View.VISIBLE else View.GONE
            binding.progressBar.progress = progress
        }
    }

    private fun setupObservers() {
        // Detection state
        viewModel?.isDetecting?.observe(this) { detecting ->
            binding.detectionIndicator.visibility = if (detecting) View.VISIBLE else View.GONE
            if (detecting) {
                binding.detectionIndicator.setBackgroundColor(getColor(R.color.detection_active))
                binding.tvStatus.text = "CODE DETECTED - Press Shutter to Capture"
                binding.tvStatus.setTextColor(getColor(R.color.status_ok))
            } else {
                binding.tvStatus.text = "Scanning..."
                binding.tvStatus.setTextColor(getColor(R.color.white))
            }
        }

        // Detected codes
        viewModel?.detectedCodes?.observe(this) { codes ->
            if (codes.isNotEmpty()) {
                val codeInfo = codes.joinToString("\n") { "${it.format}: ${it.value}" }
                binding.tvDetectedCode.text = codeInfo
                binding.tvDetectedCode.visibility = View.VISIBLE
            } else {
                binding.tvDetectedCode.visibility = View.GONE
            }
        }

        // Scan state
        viewModel?.scanState?.observe(this) { state ->
            when (state) {
                is ScanState.Idle -> {
                    binding.btnStartSession.isEnabled = true
                    binding.btnStartScan.isEnabled = false
                    binding.btnStopScan.isEnabled = false
                    binding.btnEndSession.isEnabled = false
                }
                is ScanState.SessionActive -> {
                    binding.btnStartSession.isEnabled = false
                    binding.btnStartScan.isEnabled = true
                    binding.btnStopScan.isEnabled = false
                    binding.btnEndSession.isEnabled = true
                }
                is ScanState.Scanning -> {
                    binding.btnStartSession.isEnabled = false
                    binding.btnStartScan.isEnabled = false
                    binding.btnStopScan.isEnabled = true
                    binding.btnEndSession.isEnabled = false
                }
                is ScanState.CodeCaptured -> {
                    Toast.makeText(this, "Code captured: ${state.value}", Toast.LENGTH_SHORT).show()
                }
                is ScanState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Session info
        viewModel?.sessionInfo?.observe(this) { info ->
            binding.tvSessionInfo.text = info
        }

        // Scanned codes count
        viewModel?.scannedCodes?.observe(this) { codes ->
            binding.tvCodesCount.text = "Codes Scanned: ${codes.size}"
        }

        // Location
        viewModel?.currentLocation?.observe(this) { location ->
            if (location != null) {
                binding.tvLocation.text = "GPS: ${String.format("%.6f, %.6f", location.latitude, location.longitude)}"
                binding.tvLocation.setTextColor(getColor(R.color.status_ok))
            } else {
                binding.tvLocation.text = "GPS: No signal"
                binding.tvLocation.setTextColor(getColor(R.color.status_warning))
            }
        }
    }

    private fun setupButtons() {
        binding.btnStartSession.setOnClickListener {
            viewModel?.startSession()
        }

        binding.btnStartScan.setOnClickListener {
            viewModel?.startScanning()
        }

        binding.btnStopScan.setOnClickListener {
            viewModel?.stopScanning()
        }

        binding.btnEndSession.setOnClickListener {
            showEndSessionDialog()
        }

        binding.btnExport.setOnClickListener {
            viewModel?.let { vm ->
                val (jsonFile, csvFile) = vm.exportSession()
                if (jsonFile != null && csvFile != null) {
                    Toast.makeText(this, "Exported to:\n${jsonFile.name}\n${csvFile.name}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showEndSessionDialog() {
        AlertDialog.Builder(this)
            .setTitle("End Session")
            .setMessage("Do you want to end the current scanning session? All data will be saved.")
            .setPositiveButton("End Session") { _, _ ->
                viewModel?.let { vm ->
                    val file = vm.closeSession()
                    if (file != null) {
                        Toast.makeText(this, "Session saved to: ${file.name}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions Required")
            .setMessage("This app requires camera, location, and bluetooth permissions to function properly.")
            .setPositiveButton("Grant") { _, _ ->
                checkPermissions()
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun enableControls(enabled: Boolean) {
        binding.btnStartSession.isEnabled = enabled
    }

    override fun onResume() {
        super.onResume()
        LogUtils.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        LogUtils.d(TAG, "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d(TAG, "onDestroy")
    }
}
