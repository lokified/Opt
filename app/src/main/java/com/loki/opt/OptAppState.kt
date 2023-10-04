package com.loki.opt

import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController

@Stable
class OptAppState(
    val navController: NavHostController
) {

    fun popUp() {
        navController.popBackStack()
    }

    fun navigate(route: String) {
        navController.navigate(route = route) {
            launchSingleTop = true
        }
    }

    fun navigateAndPopUp(route: String, popUp: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) {
                inclusive = true
            }
        }
    }
}