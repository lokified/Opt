package com.loki.opt.ui.viewModel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.loki.opt.PolicyManager
import com.loki.opt.data.database.Schedule
import com.loki.opt.data.datastore.DataStoreStorage
import com.loki.opt.data.repository.OptRepository
import com.loki.opt.worker.LockScreenWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptViewModel @Inject constructor(
    private val optRepository: OptRepository,
    private val dataStore: DataStoreStorage,
    private val policyManager: PolicyManager,
    context: Context
): ViewModel() {

    private val _state = MutableStateFlow(ScheduleState())
    val state = _state.asStateFlow()

    private val workManager = WorkManager.getInstance(context)
    var isLaunching = mutableStateOf(true)

    init {
        getAllSchedules()
    }

    fun getIsAdminEnabled() = policyManager.isActive()

    fun onAppLaunch(openHomeScreen: () -> Unit) {
        viewModelScope.launch {
            dataStore.getIsFirstTimeLaunch().collect { isFirstLaunch ->
                if (!isFirstLaunch) {
                    openHomeScreen()
                }
            }
        }
    }

    fun setIsFirstTimeLaunch(isFirstTime: Boolean) {
        viewModelScope.launch {
            dataStore.isFirstTimeLaunch(isFirstTime)
        }
    }

    fun onScheduleEvent(scheduleEvent: ScheduleEvent) {
        when(scheduleEvent) {
            is ScheduleEvent.OnSchedule -> saveSchedule()
            is ScheduleEvent.OnEditSchedule -> {
                _state.value = _state.value.copy(
                    id = scheduleEvent.schedule.id,
                    title = scheduleEvent.schedule.title,
                    offTime = scheduleEvent.schedule.offTime,
                    isEnabled = scheduleEvent.schedule.isEnabled
                )
            }
            is ScheduleEvent.OnEnableSchedule -> enableSchedule(scheduleEvent.isEnabled, scheduleEvent.scheduleId)
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
        }

        val lockScreenRequest = OneTimeWorkRequestBuilder<LockScreenWorker>().build()
        workManager.beginUniqueWork(
            "lock screen ${_state.value.title}",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            lockScreenRequest
        ).enqueue()
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
        }

        workManager.cancelUniqueWork("lock screen ${_state.value.title}")
    }

    private fun enableSchedule(isEnabled: Boolean, scheduleId: Int) {
        viewModelScope.launch {
            optRepository.enableSchedule(isEnabled, scheduleId)
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

    object OnSchedule: ScheduleEvent()
    data class OnEditSchedule(val schedule: Schedule): ScheduleEvent()
    data class OnEnableSchedule(val isEnabled: Boolean, val scheduleId: Int): ScheduleEvent()
    object OnDeleteSchedule: ScheduleEvent()
    data class TitleChangeEvent(val title: String): ScheduleEvent()
    data class OffTimeChangeEvent(val offTime: String): ScheduleEvent()
    data class EnabledChangeEvent(val isEnabled: Boolean): ScheduleEvent()
}