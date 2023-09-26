package com.loki.opt.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loki.opt.data.database.Schedule
import com.loki.opt.viewModel.ScheduleEvent
import com.loki.opt.viewModel.ScheduleState

@Composable
fun HomeScreen(
    scheduleState: ScheduleState,
    handleScheduleEvent: (ScheduleEvent) -> Unit,
    navigateToNewScreen: () -> Unit
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    handleScheduleEvent(
                        ScheduleEvent.OnEditSchedule(
                            Schedule(0,"", "", false)
                        )
                    )
                    navigateToNewScreen()
                }
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add_Icon")
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {

            if (scheduleState.schedules.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Nothing has been scheduled")
                }
            }

            LazyColumn(contentPadding = PaddingValues(16.dp)) {

                item {
                    Text(text = "My Schedules", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }

                items(scheduleState.schedules) { schedule ->
                    ScheduleItem(
                        schedule = schedule,
                        onEnable = {
                            handleScheduleEvent(
                                ScheduleEvent.OnEnableSchedule(
                                    isEnabled = it,
                                    scheduleId = schedule.id
                                )
                            )
                        },
                        onClickItem = {
                            navigateToNewScreen()
                            handleScheduleEvent(
                                ScheduleEvent.OnEditSchedule(schedule)
                            )
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleItem(
    modifier: Modifier = Modifier,
    schedule: Schedule,
    onEnable: (Boolean) -> Unit,
    onClickItem: () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClickItem()
            }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Column {
                Text(text = schedule.title, fontSize = 20.sp)
                Text(text = schedule.offTime, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.weight(1f))

            Switch(checked = schedule.isEnabled, onCheckedChange = onEnable)
        }
    }
}