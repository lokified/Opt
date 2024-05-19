package com.loki.opt.services

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PolicyManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val devicePolicyManager: DevicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    val componentName = ComponentName(context, MyDeviceAdminReceiver::class.java)

    fun isActive(): Boolean {
        return devicePolicyManager.isAdminActive(componentName)
    }

    fun lockScreen() {
        devicePolicyManager.lockNow()
    }
}