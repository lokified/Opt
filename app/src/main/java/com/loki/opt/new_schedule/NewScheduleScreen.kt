package com.loki.opt.new_schedule

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
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loki.opt.R
import com.loki.opt.util.ext.toTwoDigit
import com.loki.opt.viewModel.ScheduleEvent
import com.loki.opt.viewModel.ScheduleState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewScheduleScreen(
    scheduleState: ScheduleState,
    handleScheduleEvent: (ScheduleEvent) -> Unit,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current

    var selectedHour by remember { mutableIntStateOf(0) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        is24Hour = false
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.schedule))
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "icon_back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            if (showDialog) {
                AlertDialog(
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
                                Text(text = "Dismiss")
                            }

                            TextButton(
                                onClick = {
                                    showDialog = false
                                    selectedHour = timePickerState.hour
                                    selectedMinute = timePickerState.minute
                                    handleScheduleEvent(
                                        ScheduleEvent.OffTimeChangeEvent(
                                            "${selectedHour.toTwoDigit()}:${selectedMinute.toTwoDigit()}"
                                        )
                                    )
                                }
                            ) {
                                Text(text = "Confirm")
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
                            handleScheduleEvent(
                                ScheduleEvent.TitleChangeEvent(it)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(text = "Name Title")
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
                                Text(text = "Lock Time", fontSize = 18.sp)
                                Text(
                                    text = scheduleState.offTime,
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

                    if (scheduleState.title.isNotBlank()) {

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            TextButton(
                                onClick = {
                                    handleScheduleEvent(ScheduleEvent.OnDeleteSchedule)
                                    navigateBack()
                                }
                            ) {
                                Text(text = "Delete Schedule")
                            }
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

                TextButton(
                    onClick = {
                        handleScheduleEvent(
                            ScheduleEvent.OnSchedule(context)
                        )
                        navigateBack()
                    },
                    enabled = scheduleState.title != ""
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}