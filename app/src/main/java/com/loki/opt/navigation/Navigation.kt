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
                }
            )
        }

        composable(
            route = Screen.NewScheduleScreen.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(durationMillis = 400)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(durationMillis = 200)
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
    }
}

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")
    object NewScheduleScreen: Screen("new_schedule_screen")
}