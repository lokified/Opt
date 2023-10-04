package com.loki.opt.home

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loki.opt.MyDeviceAdminReceiver
import com.loki.opt.R
import com.loki.opt.components.AdminPermissionPanel
import com.loki.opt.data.database.Schedule
import com.loki.opt.viewModel.ScheduleEvent
import com.loki.opt.viewModel.ScheduleState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isAdminEnabled: Boolean,
    scheduleState: ScheduleState,
    handleScheduleEvent: (ScheduleEvent) -> Unit,
    navigateToNewScreen: () -> Unit,
    navigateToSettings: () -> Unit
) {

    val context = LocalContext.current
    var isAdminPanelVisible by remember { mutableStateOf(false) }

    val deviceAdminLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            isAdminPanelVisible = false

            Toast.makeText(
                context,
                context.getString(R.string.opt_is_now_an_admin),
                Toast.LENGTH_LONG
            ).show()
        }
        else {
            Toast.makeText(
                context,
                context.getString(R.string.admin_permission_cancelled),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (!isAdminEnabled) {
            isAdminPanelVisible = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.my_schedules),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = navigateToSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "settings_icon"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
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
                    Text(text = stringResource(R.string.nothing_has_been_scheduled))
                }
            }

            if (isAdminPanelVisible) {
                AdminPermissionPanel(
                    onDismiss = {},
                    onRequest = {
                        deviceAdminLauncher.launch(requestPermission(context))
                    }
                )
            }

            LazyColumn(contentPadding = PaddingValues(16.dp)) {
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

fun requestPermission(context: Context): Intent {
    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
    intent.putExtra(
        DevicePolicyManager.EXTRA_DEVICE_ADMIN,
        ComponentName(context, MyDeviceAdminReceiver::class.java)
    )
    intent.putExtra(
        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
        context.getString(R.string.the_app_needs_admin)
    )
    return intent
}