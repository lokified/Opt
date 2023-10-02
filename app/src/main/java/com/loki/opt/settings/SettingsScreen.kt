package com.loki.opt.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loki.opt.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingState: SettingState,
    onHandleSettingsEvent: (SettingsEvent) -> Unit,
    navigateBack: () -> Unit
) {
    
    Scaffold(
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
        },
        
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = stringResource(R.string.enable_disable_activities),
                modifier = Modifier.padding(
                    vertical = 4.dp,
                    horizontal = 16.dp
                ),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            SettingRowItem(
                title = stringResource(R.string.stop_playing_music),
                description = stringResource(R.string.music_will_stop),
                isChecked = settingState.musicToStop,
                onChecked = {
                    onHandleSettingsEvent(
                        SettingsEvent.OnMusicPlayingChange(it)
                    )
                }
            )
        }
    }
}

@Composable
fun SettingRowItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    isChecked: Boolean,
    onChecked: (Boolean) -> Unit
) {

    Box(modifier = modifier.fillMaxWidth()) {

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
        ) {

            Column {
                Text(text = title, fontSize = 18.sp)
                Text(text = description, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Switch(checked = isChecked, onCheckedChange = onChecked)
        }
    }
}