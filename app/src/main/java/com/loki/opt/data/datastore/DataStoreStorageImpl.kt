package com.loki.opt.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.loki.opt.data.datastore.DataStoreStorage.SettingPreference.FIRST_LAUNCH
import com.loki.opt.data.datastore.DataStoreStorage.SettingPreference.MUSIC_TO_STOP
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreStorageImpl @Inject constructor(
    private val datastore: DataStore<Preferences>
): DataStoreStorage {

    override suspend fun setActivitySetting(activitySetting: ActivitySetting) {
        datastore.edit { preference ->
            preference[MUSIC_TO_STOP] = activitySetting.isMusicToStop
        }
    }

    override fun getActivitySetting(): Flow<ActivitySetting> {
        return datastore.data.map { preference ->
            val isMusicToStop = preference[MUSIC_TO_STOP] ?: false

            ActivitySetting(isMusicToStop)
        }
    }

    override suspend fun isFirstTimeLaunch(isFirstTime: Boolean) {
        datastore.edit { preference ->
            preference[FIRST_LAUNCH] = isFirstTime
        }
    }

    override fun getIsFirstTimeLaunch(): Flow<Boolean> {
        return datastore.data.map { preference ->
            preference[FIRST_LAUNCH] ?: true
        }
    }
}