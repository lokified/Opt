package com.loki.opt.ui.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.loki.opt.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigator: DestinationsNavigator
) {

    val context = LocalContext.current
    val settingsViewModel = hiltViewModel<SettingsViewModel>()
    val settingState by settingsViewModel.state.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val overlaySettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}

    if (settingState.fullScreenNotification) {

        LaunchedEffect(key1 = settingState.fullScreenNotification) {

            val intent = checkOverLayPermission(context)

            if (intent != null) {
                overlaySettingsLauncher.launch(checkOverLayPermission(context))
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Want to change app overlay permission?",
                        actionLabel = "Open Settings"
                    )
                }
            }
        }
    } else {
        snackbarHostState.currentSnackbarData?.dismiss()
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(100.dp)
                        .background(
                            color = MaterialTheme.colorScheme.scrim,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                ) {
                    Text(text = it.visuals.message, modifier = Modifier.align(Alignment.TopStart))
                    TextButton(
                        onClick = {
                            it.dismiss()
                        },
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        Text(text = "Dismiss")
                    }
                    TextButton(
                        onClick = {
                            overlaySettingsLauncher.launch(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
                        },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Text(text = it.visuals.actionLabel!!)
                    }
                }
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigator::navigateUp) {
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
        },

        ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            SettingsHeader(title = stringResource(R.string.enable_disable_activities))

            SettingRowItem(
                title = stringResource(R.string.stop_playing_music),
                description = stringResource(R.string.music_will_stop),
                isChecked = settingState.musicToStop,
                onContainerClicked = {
                    settingsViewModel.onSettingsEvent(
                        SettingsEvent.OnMusicPlayingChange(it)
                    )
                }
            )

            SettingsHeader(title = stringResource(R.string.notifications))

            SettingRowItem(
                title = stringResource(R.string.full_screen_notifications),
                description = stringResource(R.string.this_will_show_a_full_screen_notification),
                isChecked = settingState.fullScreenNotification,
                onContainerClicked = {
                    settingsViewModel.onSettingsEvent(
                        SettingsEvent.OnFullScreenNotificationChange(it)
                    )
                }
            )
        }
    }
}

@Composable
fun SettingsHeader(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        text = title,
        modifier = modifier.padding(
            vertical = 4.dp,
            horizontal = 16.dp
        ),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun SettingRowItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    isChecked: Boolean,
    onContainerClicked: (Boolean) -> Unit
) {

    var isCheckedChange by remember { mutableStateOf(isChecked) }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
        ) {

            Column(modifier = Modifier.weight(.9f)) {
                Text(text = title, fontSize = 18.sp)
                Text(text = description, fontSize = 14.sp)
            }

            Switch(
                checked = isCheckedChange,
                onCheckedChange = {
                    isCheckedChange = it
                },
                modifier = Modifier.weight(.1f)
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable {
                    isCheckedChange = !isCheckedChange
                    onContainerClicked(isCheckedChange)
                }
        )
    }
}

fun checkOverLayPermission(context: Context): Intent? {

    var intent: Intent? = null
    if (!Settings.canDrawOverlays(context)) {
        intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    }
    return intent
}