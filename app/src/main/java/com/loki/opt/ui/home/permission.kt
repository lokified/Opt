package com.loki.opt.ui.home

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.content.ContextCompat
import com.loki.opt.R
import com.loki.opt.services.MyDeviceAdminReceiver

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationPermissionRequest(
    context: Context,
    onGrantedPermission: (isGranted: Boolean) -> Unit
) {
    val isGranted = checkIfPermissionGranted(context, Manifest.permission.POST_NOTIFICATIONS)

    if (isGranted){
        onGrantedPermission(true)
    } else {

        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            onGrantedPermission(result)
        }

        SideEffect {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

fun checkIfPermissionGranted(context: Context, permission: String): Boolean {
    return (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED)
}

fun requestAdminPermission(context: Context): Intent {
    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
    intent.putExtra(
        DevicePolicyManager.EXTRA_DEVICE_ADMIN,
        ComponentName(context, MyDeviceAdminReceiver::class.java)
    )
    intent.putExtra(
        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
        context.getString(R.string.the_app_needs_admin)
    )
    return intent
}