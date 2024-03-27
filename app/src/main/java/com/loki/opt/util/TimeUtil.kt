package com.loki.opt.util

import java.util.Calendar

object TimeUtil {

    fun getCurrentTime(): Pair<Int, Int> {
        val currentTime = Calendar.getInstance()
        val initialHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val initialMinute = currentTime.get(Calendar.MINUTE)

        return Pair(initialHour, initialMinute)
    }

    fun getSuggestedTime(): Pair<Int, Int> {
        val (currentHour, currentMinute) = getCurrentTime()

        val minute: Int
        val hour: Int

        if (currentMinute < 30) {
            minute = currentMinute + 30
            hour = currentHour
        }
        else if (currentMinute == 30) {
            minute = 0
            hour = currentHour + 1
        }
        else {
            minute = (30 + currentMinute) - 60
            hour = currentHour + 1
        }

        val isMidnightHour = if (hour == 24) 0 else hour

        return Pair(isMidnightHour, minute)
    }
}