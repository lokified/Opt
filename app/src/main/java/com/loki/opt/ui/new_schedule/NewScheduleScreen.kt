package com.loki.opt.ui.new_schedule

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loki.opt.R
import com.loki.opt.ui.viewModel.OptViewModel
import com.loki.opt.ui.viewModel.ScheduleEvent
import com.loki.opt.ui.viewModel.ScheduleState
import com.loki.opt.util.TimeUtil
import com.loki.opt.util.ext.toTwoDigit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewScheduleScreen(
    optViewModel: OptViewModel,
    scheduleState: ScheduleState,
    isEditScreen: Boolean,
    navigateBack: () -> Unit
) {

    val (hour, minute) = TimeUtil.getSuggestedTime()

    var selectedHour by remember { mutableIntStateOf(hour) }
    var selectedMinute by remember { mutableIntStateOf(minute) }
    var showDialog by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        is24Hour = false
    )

    BackHandler {
        navigateBack()
    }

    Scaffold { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            if (showDialog) {
                BasicAlertDialog(
                    onDismissRequest = {
                        showDialog = false
                    }
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        TimePicker(
                            state = timePickerState
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {

                            TextButton(onClick = { showDialog = false }) {
                                Text(text = stringResource(id = R.string.dismiss))
                            }

                            TextButton(
                                onClick = {
                                    showDialog = false
                                    selectedHour = timePickerState.hour
                                    selectedMinute = timePickerState.minute
                                    optViewModel.onScheduleEvent(
                                        ScheduleEvent.OffTimeChangeEvent(
                                            "${selectedHour.toTwoDigit()}:${selectedMinute.toTwoDigit()}"
                                        )
                                    )
                                }
                            ) {
                                Text(text = stringResource(R.string.confirm))
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextField(
                        value = scheduleState.title,
                        onValueChange = {
                            optViewModel.onScheduleEvent(
                                ScheduleEvent.TitleChangeEvent(it)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.lock_name_title),
                                fontSize = 24.sp,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onBackground.copy(.5f)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                showDialog = true
                            }
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.select_lock_time),
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = if (scheduleState.offTime.isNotBlank()) scheduleState.offTime
                                    else "${hour.toTwoDigit()}:${minute.toTwoDigit()}",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                imageVector = Icons.Filled.AccessTimeFilled,
                                contentDescription = "clock_icon"
                            )
                        }
                    }
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    ),
                horizontalArrangement = Arrangement.End
            ) {

                if (isEditScreen) {

                    TextButton(
                        onClick = {
                            optViewModel.onScheduleEvent(ScheduleEvent.OnDeleteSchedule)
                            navigateBack()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(.5f),
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = stringResource(R.string.delete_schedule))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = {
                        optViewModel.onScheduleEvent(
                            ScheduleEvent.OffTimeChangeEvent(
                                "${selectedHour.toTwoDigit()}:${selectedMinute.toTwoDigit()}"
                            )
                        )
                        optViewModel.onScheduleEvent(ScheduleEvent.OnSchedule)
                        navigateBack()
                    },
                    enabled = scheduleState.title.isNotBlank(),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(.5f),
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (scheduleState.title.isNotBlank()) MaterialTheme.colorScheme.primary
                        else Color.Gray,
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }
}