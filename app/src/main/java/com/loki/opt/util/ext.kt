package com.loki.opt.util

import android.content.Context
import android.content.Intent
import com.loki.opt.notification.NotificationForegroundService
import com.loki.opt.services.LockForegroundService

fun Int.toTwoDigit(): String {
    val value = this.toString()
    return if (value.length == 2) value else "0$this"
}

fun String.toSingleDigit(): String {
    return if (this.startsWith("0")) this.removePrefix("0") else this
}

fun Context.startNotifService() {
    startService(Intent(this, NotificationForegroundService::class.java))
}

fun Context.stopNotifService() {
    stopService(Intent(this, NotificationForegroundService::class.java))
}

fun Context.startLockService() {
    startService(Intent(this, LockForegroundService::class.java))
}

fun Context.stopLockService() {
    stopService(Intent(this, LockForegroundService::class.java))
}