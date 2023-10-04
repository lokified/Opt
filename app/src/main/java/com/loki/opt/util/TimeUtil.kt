package com.loki.opt.util

import com.loki.opt.util.ext.toTwoDigit
import java.util.Calendar

object TimeUtil {

    fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY).toTwoDigit()
        val minute = currentTime.get(Calendar.MINUTE).toTwoDigit()

        return "$hour:$minute"
    }
}