package com.loki.opt.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loki.opt.data.datastore.DataStoreStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val datastore: DataStoreStorage
): ViewModel() {

    private val _state = MutableStateFlow(SettingState())
    val state = _state.asStateFlow()

    init {
        getSettings()
    }

    fun onSettingsEvent(settingsEvent: SettingsEvent) {
        when(settingsEvent) {
            is SettingsEvent.OnMusicPlayingChange -> onMusicPlayingChange(settingsEvent.isStopMusic)
        }
    }

    private fun getSettings() {

        viewModelScope.launch {
            datastore.getActivitySetting().collect { activity ->
                _state.value = SettingState(
                    musicToStop = activity.isMusicToStop
                )
            }
        }
    }

    private fun onMusicPlayingChange(isStopMusic: Boolean) {
        viewModelScope.launch {
            datastore.setActivitySetting(
                _state.value.copy(
                    musicToStop = isStopMusic
                ).toActivitySetting()
            )
        }
    }
}

sealed class SettingsEvent {
    data class OnMusicPlayingChange(val isStopMusic: Boolean): SettingsEvent()
}