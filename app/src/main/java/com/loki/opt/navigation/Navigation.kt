package com.loki.opt.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.loki.opt.home.HomeScreen
import com.loki.opt.new_schedule.NewScheduleScreen
import com.loki.opt.settings.SettingsScreen
import com.loki.opt.settings.SettingsViewModel
import com.loki.opt.viewModel.OptViewModel

@Composable
fun Navigation(
    navController: NavHostController
) {

    val optViewModel = hiltViewModel<OptViewModel>()
    val state by optViewModel.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {

        composable(route = Screen.HomeScreen.route) {

            HomeScreen(
                scheduleState = state,
                handleScheduleEvent = optViewModel::onScheduleEvent,
                navigateToNewScreen = {
                    navController.navigate(Screen.NewScheduleScreen.route)
                },
                navigateToSettings = {
                    navController.navigate(Screen.SettingsScreen.route)
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
                navigateBack = {
                    navController.navigateUp()
                }
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
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")
    object NewScheduleScreen: Screen("new_schedule_screen")
    object SettingsScreen: Screen("settings_screen")
}