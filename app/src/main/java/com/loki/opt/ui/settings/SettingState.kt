package com.loki.opt.ui.settings

import com.loki.opt.data.datastore.ActivitySetting

data class SettingState(
    val musicToStop: Boolean = false,
    val fullScreenNotification: Boolean = false
)

fun SettingState.toActivitySetting(): ActivitySetting {
    return ActivitySetting(
        isMusicToStop = musicToStop,
        isFullScreenNotification = fullScreenNotification
    )
}