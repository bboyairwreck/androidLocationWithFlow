package com.ericchee.coroutinelocationexample

import android.Manifest
import android.location.Criteria
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow

@OptIn(ExperimentalCoroutinesApi::class)
@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
fun LocationManager.locationUpdates(provider: String, minTimeMs: Long, minDistance: Float = 0f) =
    callbackFlow {
        val listener = LocationListener { location -> trySendBlocking(location) }
        requestLocationUpdates(provider, minTimeMs, minDistance, listener)
        awaitClose {
            Log.i("echee", "stopping loc updates")
            removeUpdates(listener)
        }
    }

val LocationManager.lowAccuracyProviderName get() = getBestProvider(Criteria().apply {
    horizontalAccuracy = Criteria.ACCURACY_LOW
    powerRequirement = Criteria.POWER_LOW
}, true)