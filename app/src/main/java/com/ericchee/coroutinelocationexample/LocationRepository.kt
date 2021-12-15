package com.ericchee.coroutinelocationexample

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import kotlin.jvm.Throws

class LocationRepository(
    private val locationManager: LocationManager
) {

    @SuppressLint("MissingPermission")
    fun getLocationFlow(): Flow<Location> =
        locationManager.locationUpdates(requireNotNull(locationManager.lowAccuracyProviderName), 0, 0f)

    /**
     * @throws TimeoutCancellationException
     */
    @Throws(TimeoutCancellationException::class)
    suspend fun getLocationWithTimeout(timeoutMS: Long): Location = withTimeout(timeoutMS) {
        getLocationFlow().first()
    }

}

