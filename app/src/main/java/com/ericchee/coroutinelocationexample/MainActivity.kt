package com.ericchee.coroutinelocationexample

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ericchee.coroutinelocationexample.databinding.ActivityMainBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val locationRepository by lazy { LocationRepository(locationManager) }

    private val fineCoarseLocationResultLauncher = registerForActivityResult(RequestMultiplePermissions(), ::onFineCoarsePermissionGrantResults)

    private var fetchLocationJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(ActivityMainBinding.inflate(layoutInflater).also { binding = it; setContentView(it.root) }) {

            btnFetchLocation.setOnClickListener {
                when {
                    hasLocationPermissions() -> fetchLocation()
                    else -> fineCoarseLocationResultLauncher.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
                }
            }

            btnStop.setOnClickListener { fetchLocationJob?.cancel() }
        }
    }

    override fun onPause() {
        super.onPause()
        fetchLocationJob?.cancel()
    }

    private fun onFineCoarsePermissionGrantResults(permissions: Map<String, Boolean>) {
        val isFineGranted = permissions[ACCESS_FINE_LOCATION] ?: false
        val isCoarseGranted = permissions[ACCESS_COARSE_LOCATION] ?: false

        when {
            isFineGranted -> fetchLocation()
            isCoarseGranted -> fetchLocation()
            else -> Toast.makeText(this, "Need permissions", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchLocation() {
        fetchLocationJob?.cancel()

        lifecycleScope.launch {

            locationRepository.getLocationFlow().collect {
                Log.i("echee", "loc: ${it.latitude}, ${it.longitude}")
            }
        }.also { fetchLocationJob = it }

//        lifecycleScope.launch {
//            val location = runCatching { locationRepository.getLocationWithTimeout(5000) }.onFailure {
//                if (it is TimeoutCancellationException) {
//                } }.getOrNull()
//
//            locationRepository.setCoor
//
//            navigateHOme
//        }
    }

    private fun hasLocationPermissions() = isPermissionGranted(ACCESS_COARSE_LOCATION) || isPermissionGranted(ACCESS_FINE_LOCATION)
}



private val Context.locationManager get() = requireNotNull(ContextCompat.getSystemService(this, LocationManager::class.java))