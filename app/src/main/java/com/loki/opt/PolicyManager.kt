package com.loki.opt

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import javax.inject.Inject

class PolicyManager @Inject constructor(
    context: Context
) {

    private val devicePolicyManager: DevicePolicyManager by lazy {
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }
    val componentName = ComponentName(context, MyDeviceAdminReceiver::class.java)

    fun isActive(): Boolean {
        return devicePolicyManager.isAdminActive(componentName)
    }

    fun lockScreen() {
        devicePolicyManager.lockNow()
    }
}