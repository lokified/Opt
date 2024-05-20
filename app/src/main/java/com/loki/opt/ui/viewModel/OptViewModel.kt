package com.loki.opt.ui.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loki.opt.services.PolicyManager
import com.loki.opt.data.database.Schedule
import com.loki.opt.data.datastore.DataStoreStorage
import com.loki.opt.data.repository.OptRepository
import com.loki.opt.worker.WorkInitializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptViewModel @Inject constructor(
    private val optRepository: OptRepository,
    private val dataStore: DataStoreStorage,
    private val policyManager: PolicyManager,
    private val workInitializer: WorkInitializer
) : ViewModel() {

    private val _state = MutableStateFlow(ScheduleState())
    val state = _state.asStateFlow()

    var isLaunching = mutableStateOf(true)

    init {
        getAllSchedules()
    }

    fun getIsAdminEnabled() = policyManager.isActive()

    val isFirstTimeLaunch = dataStore.getIsFirstTimeLaunch().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        false
    )

    fun setIsFirstTimeLaunch(isFirstTime: Boolean) {
        viewModelScope.launch {
            dataStore.isFirstTimeLaunch(isFirstTime)
        }
    }

    fun onScheduleEvent(scheduleEvent: ScheduleEvent) {
        when (scheduleEvent) {
            is ScheduleEvent.OnSchedule -> saveSchedule()
            is ScheduleEvent.OnEditSchedule -> {
                _state.value = _state.value.copy(
                    id = scheduleEvent.schedule.id,
                    title = scheduleEvent.schedule.title,
                    offTime = scheduleEvent.schedule.offTime,
                    isEnabled = scheduleEvent.schedule.isEnabled
                )
            }

            is ScheduleEvent.OnEnableSchedule -> enableSchedule(
                scheduleEvent.title,
                scheduleEvent.isEnabled,
                scheduleEvent.scheduleId
            )

            is ScheduleEvent.OnDeleteSchedule -> deleteSchedule()
            is ScheduleEvent.TitleChangeEvent -> onTitleChange(scheduleEvent.title)
            is ScheduleEvent.OffTimeChangeEvent -> onOffTimeChange(scheduleEvent.offTime)
            is ScheduleEvent.EnabledChangeEvent -> onEnabledChange(scheduleEvent.isEnabled)
        }
    }

    private fun getAllSchedules() {
        viewModelScope.launch {
            optRepository.getAllSchedules().collect { schedules ->
                _state.value = ScheduleState(
                    schedules = schedules
                )
            }
        }
    }

    private fun saveSchedule() {
        viewModelScope.launch {
            optRepository.saveSchedule(
                Schedule(
                    id = _state.value.id,
                    title = _state.value.title,
                    offTime = _state.value.offTime,
                    isEnabled = true
                )
            )
            workInitializer.initialize(workName = "lock screen ${_state.value.title}")
        }
    }

    private fun deleteSchedule() {
        viewModelScope.launch {
            optRepository.deleteSchedule(
                Schedule(
                    id = _state.value.id,
                    title = _state.value.title,
                    offTime = _state.value.offTime,
                    isEnabled = _state.value.isEnabled
                )
            )
            workInitializer.cancelWork("lock screen ${_state.value.title}")
        }
    }

    private fun enableSchedule(title: String, isEnabled: Boolean, scheduleId: Int) {
        viewModelScope.launch {
            optRepository.enableSchedule(isEnabled, scheduleId)
            if (isEnabled) {
                workInitializer.initialize(workName = "lock screen $title")
            }
        }
    }

    private fun onTitleChange(newValue: String) {
        _state.value = _state.value.copy(
            title = newValue
        )
    }

    private fun onOffTimeChange(newValue: String) {
        _state.value = _state.value.copy(
            offTime = newValue
        )
    }

    private fun onEnabledChange(newValue: Boolean) {
        _state.value = _state.value.copy(
            isEnabled = newValue
        )
    }

}

sealed class ScheduleEvent {

    data object OnSchedule : ScheduleEvent()
    data class OnEditSchedule(val schedule: Schedule) : ScheduleEvent()
    data class OnEnableSchedule(val title: String, val isEnabled: Boolean, val scheduleId: Int) : ScheduleEvent()
    data object OnDeleteSchedule : ScheduleEvent()
    data class TitleChangeEvent(val title: String) : ScheduleEvent()
    data class OffTimeChangeEvent(val offTime: String) : ScheduleEvent()
    data class EnabledChangeEvent(val isEnabled: Boolean) : ScheduleEvent()
}