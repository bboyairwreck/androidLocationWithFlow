package com.ericchee.coroutinelocationexample

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.isPermissionGranted(permission: String): Boolean {
    val permissionCheck = ContextCompat.checkSelfPermission(this, permission)
    return permissionCheck == PackageManager.PERMISSION_GRANTED
}