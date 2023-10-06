package com.loki.opt.util

import java.util.Calendar

object TimeUtil {

    fun getSuggestedTime(): Pair<Int, Int> {
        val currentTime = Calendar.getInstance()
        val initialHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val initialMinute = currentTime.get(Calendar.MINUTE)

        val minute: Int
        val hour: Int

        if (initialMinute < 30) {
            minute = initialMinute + 30
            hour = initialHour
        }
        else if (initialMinute == 30) {
            minute = 0
            hour = initialHour + 1
        }
        else {
            minute = (30 + initialMinute) - 60
            hour = initialHour + 1
        }

        val isMidnightHour = if (hour == 24) 0 else hour

        return Pair(isMidnightHour, minute)
    }
}