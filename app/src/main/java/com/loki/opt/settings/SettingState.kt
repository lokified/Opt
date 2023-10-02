package com.loki.opt.settings

import com.loki.opt.data.datastore.ActivitySetting

data class SettingState(
    val musicToStop: Boolean = false
)

fun SettingState.toActivitySetting(): ActivitySetting {
    return ActivitySetting(
        isMusicToStop = musicToStop
    )
}