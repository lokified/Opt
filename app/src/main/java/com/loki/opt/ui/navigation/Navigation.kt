package com.loki.opt.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.loki.opt.OptAppState
import com.loki.opt.ui.home.HomeScreen
import com.loki.opt.ui.new_schedule.NewScheduleScreen
import com.loki.opt.ui.onboarding.OnBoardingScreen
import com.loki.opt.ui.settings.SettingsScreen
import com.loki.opt.ui.settings.SettingsViewModel
import com.loki.opt.ui.viewModel.OptViewModel

@Composable
fun Navigation(
    appState: OptAppState
) {

    val optViewModel = hiltViewModel<OptViewModel>()
    val state by optViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        optViewModel.onAppLaunch {
            appState.navigateAndPopUp(Screen.HomeScreen.route, Screen.OnBoardingScreen.route)
        }
        kotlinx.coroutines.delay(200L)
        optViewModel.isLaunching.value = false
    }

    NavHost(
        navController = appState.navController,
        startDestination = Screen.OnBoardingScreen.route
    ) {

        composable(route = Screen.OnBoardingScreen.route) {

            if (!optViewModel.isLaunching.value) {
                OnBoardingScreen(
                    navigateToHome = {
                        appState.navigateAndPopUp(
                            Screen.HomeScreen.route,
                            Screen.OnBoardingScreen.route
                        )
                        optViewModel.setIsFirstTimeLaunch(false)
                    }
                )
            }
        }

        composable(route = Screen.HomeScreen.route) {

            HomeScreen(
                isAdminEnabled = optViewModel.getIsAdminEnabled(),
                scheduleState = state,
                handleScheduleEvent = optViewModel::onScheduleEvent,
                navigateToNewScreen = {
                    appState.navigate(Screen.NewScheduleScreen.route)
                },
                navigateToSettings = {
                    appState.navigate(Screen.SettingsScreen.route)
                }
            )
        }

        composable(
            route = Screen.NewScheduleScreen.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(durationMillis = 100),
                    initialOffset = { it/6 }
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(durationMillis = 100),
                    targetOffset = { it/6 }
                )
            }
        ) {

            NewScheduleScreen(
                scheduleState = state,
                handleScheduleEvent = optViewModel::onScheduleEvent,
                navigateBack = appState::popUp
            )
        }

        composable(
            route = Screen.SettingsScreen.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 200)
                )
            }
        ) {

            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            val settingState by settingsViewModel.state.collectAsStateWithLifecycle()

            SettingsScreen(
                settingState = settingState,
                onHandleSettingsEvent = settingsViewModel::onSettingsEvent,
                navigateBack = appState::popUp
            )
        }
    }
}

sealed class Screen(val route: String) {
    object OnBoardingScreen: Screen("onboarding_screen")
    object HomeScreen: Screen("home_screen")
    object NewScheduleScreen: Screen("new_schedule_screen")
    object SettingsScreen: Screen("settings_screen")
}