package com.loki.opt.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow

interface DataStoreStorage {

    suspend fun setActivitySetting(activitySetting: ActivitySetting)
    fun getActivitySetting(): Flow<ActivitySetting>

    suspend fun isFirstTimeLaunch(isFirstTime: Boolean)
    fun getIsFirstTimeLaunch(): Flow<Boolean>

    object SettingPreference {
        val MUSIC_TO_STOP = booleanPreferencesKey("music_stop_key")
        val FULL_SCREEN_NOTIFICATION = booleanPreferencesKey("full_screen_notification_key")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }
}