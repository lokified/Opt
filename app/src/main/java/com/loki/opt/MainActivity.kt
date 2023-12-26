package com.loki.opt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.loki.opt.ui.NavGraphs
import com.loki.opt.ui.destinations.HomeScreenDestination
import com.loki.opt.ui.destinations.NewScheduleScreenDestination
import com.loki.opt.ui.home.HomeScreen
import com.loki.opt.ui.new_schedule.NewScheduleScreen
import com.loki.opt.ui.theme.OptTheme
import com.loki.opt.ui.viewModel.OptViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.rememberNavHostEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val optViewModel: OptViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                optViewModel.isLaunching.value
            }
        }

        setContent {
            val state by optViewModel.state.collectAsStateWithLifecycle()
            val isFirstTimeLaunch by optViewModel.isFirstTimeLaunch.collectAsStateWithLifecycle()
            val startRoute =
                if (isFirstTimeLaunch) NavGraphs.root.startRoute else HomeScreenDestination

            OptTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val engine = rememberNavHostEngine()

                    LaunchedEffect(key1 = optViewModel.isLaunching) {
                        delay(200L)
                        optViewModel.isLaunching.value = false
                    }

                    if (!optViewModel.isLaunching.value) {
                        DestinationsNavHost(
                            engine = engine,
                            navController = engine.rememberNavController(),
                            navGraph = NavGraphs.root,
                            startRoute = startRoute
                        ) {

                            composable(HomeScreenDestination) {
                                HomeScreen(
                                    navigator = destinationsNavigator,
                                    optViewModel = optViewModel,
                                    scheduleState = state
                                )
                            }

                            composable(NewScheduleScreenDestination) {
                                NewScheduleScreen(
                                    navigator = destinationsNavigator,
                                    optViewModel = optViewModel,
                                    scheduleState = state,
                                    navArgs = navArgs
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}