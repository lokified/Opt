package com.loki.opt.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow

interface DataStoreStorage {

    suspend fun setActivitySetting(activitySetting: ActivitySetting)
    fun getActivitySetting(): Flow<ActivitySetting>
    object SettingPreference {
        val MUSIC_TO_STOP = booleanPreferencesKey("music_stop_key")
    }
}