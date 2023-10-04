package com.loki.opt.ui.viewModel

import com.loki.opt.data.database.Schedule
import com.loki.opt.util.TimeUtil

data class ScheduleState(
    val id: Int = 0,
    val title: String = "",
    val offTime: String = "",
    val isEnabled: Boolean = false,
    val schedules: List<Schedule> = emptyList()
)