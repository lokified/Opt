package com.loki.opt.ui.home

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loki.opt.services.MyDeviceAdminReceiver
import com.loki.opt.R
import com.loki.opt.data.database.Schedule
import com.loki.opt.ui.components.AdminPermissionPanel
import com.loki.opt.ui.components.HomeTopBar
import com.loki.opt.ui.components.NewScheduleTopBar
import com.loki.opt.ui.destinations.SettingsScreenDestination
import com.loki.opt.ui.new_schedule.NewScheduleScreen
import com.loki.opt.ui.viewModel.OptViewModel
import com.loki.opt.ui.viewModel.ScheduleEvent
import com.loki.opt.ui.viewModel.ScheduleState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

enum class ContainerState {
    Fab,
    Fullscreen,
}

@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    optViewModel: OptViewModel,
    scheduleState: ScheduleState
) {

    val isAdminEnabled = optViewModel.getIsAdminEnabled()

    val context = LocalContext.current
    var isEditScreen by remember { mutableStateOf(false) }
    var isAdminPanelVisible by remember { mutableStateOf(false) }
    var containerState by remember { mutableStateOf(ContainerState.Fab) }

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
        } else {
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
            if (containerState == ContainerState.Fab) {
                HomeTopBar {
                    navigator.navigate(SettingsScreenDestination)
                }
            } else {
                NewScheduleTopBar {
                    containerState = ContainerState.Fab
                    isEditScreen = false
                }
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
                            optViewModel.onScheduleEvent(
                                ScheduleEvent.OnEnableSchedule(
                                    title = schedule.title,
                                    isEnabled = it,
                                    scheduleId = schedule.id
                                )
                            )
                        },
                        onClickItem = {
                            optViewModel.onScheduleEvent(
                                ScheduleEvent.OnEditSchedule(schedule)
                            )
                            containerState = ContainerState.Fullscreen
                            isEditScreen = true
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            FabContainer(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                optViewModel = optViewModel,
                scheduleState = scheduleState,
                isEditScreen = isEditScreen,
                containerState = containerState
            ) { state ->
                containerState = state
                if (state == ContainerState.Fab && isEditScreen) {
                    isEditScreen = false
                }
            }
        }
    }
}

@Composable
fun FabContainer(
    modifier: Modifier = Modifier,
    optViewModel: OptViewModel,
    scheduleState: ScheduleState,
    isEditScreen: Boolean,
    containerState: ContainerState,
    selectedContainer: (ContainerState) -> Unit
) {

    if (isEditScreen) {
        NewScheduleScreen(
            optViewModel = optViewModel,
            scheduleState = scheduleState,
            isEditScreen = isEditScreen,
            navigateBack = {
                selectedContainer(ContainerState.Fab)
            }
        )
    } else {

        val transition = updateTransition(targetState = containerState, label = "transition")

        val cornerRadius by transition.animateDp(
            label = "corner radius",
            transitionSpec = {
                when (targetState) {
                    ContainerState.Fab -> tween(
                        durationMillis = 400,
                        easing = EaseOutCubic
                    )

                    ContainerState.Fullscreen -> tween(
                        durationMillis = 200,
                        easing = FastOutSlowInEasing
                    )
                }
            }
        ) { state ->
            when (state) {
                ContainerState.Fullscreen -> 0.dp
                ContainerState.Fab -> 22.dp
            }
        }

        val backgroundColor by transition.animateColor(label = "background color") { state ->
            when (state) {
                ContainerState.Fab -> MaterialTheme.colorScheme.primary
                ContainerState.Fullscreen -> MaterialTheme.colorScheme.background
            }
        }

        val elevation by transition.animateDp(
            label = "elevation",
            transitionSpec = {
                when (targetState) {
                    ContainerState.Fab -> tween(
                        durationMillis = 400,
                        easing = EaseOutCubic
                    )

                    ContainerState.Fullscreen -> tween(
                        durationMillis = 200,
                        easing = EaseOutCubic
                    )
                }
            }
        ) { state ->
            when (state) {
                ContainerState.Fab -> 6.dp
                ContainerState.Fullscreen -> 0.dp
            }
        }

        val padding by transition.animateDp(label = "padding") { state ->
            when (state) {
                ContainerState.Fab -> 16.dp
                ContainerState.Fullscreen -> 0.dp
            }
        }

        transition.AnimatedContent(
            modifier = modifier
                .padding(end = padding, bottom = padding)
                .shadow(
                    elevation = elevation,
                    shape = RoundedCornerShape(cornerRadius)
                )
                .drawBehind {
                    drawRect(color = backgroundColor)
                }
        ) { state ->

            when (state) {
                ContainerState.Fab -> {
                    Fab(
                        onClick = {
                            selectedContainer(ContainerState.Fullscreen)

                            optViewModel.onScheduleEvent(
                                ScheduleEvent.OnEditSchedule(
                                    Schedule(0, "", "", false)
                                )
                            )
                        }
                    )
                }

                ContainerState.Fullscreen -> {

                    NewScheduleScreen(
                        optViewModel = optViewModel,
                        scheduleState = scheduleState,
                        isEditScreen = isEditScreen,
                        navigateBack = {
                            selectedContainer(ContainerState.Fab)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Fab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = 65.dp,
                minHeight = 65.dp,
            )
            .clickable(
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            tint = Color.White
        )
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