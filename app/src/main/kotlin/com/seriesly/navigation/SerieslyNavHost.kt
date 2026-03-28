package com.seriesly.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.seriesly.feature.auth.navigation.authNavGraph

@Composable
fun SerieslyNavHost(
    startDestination: String = "auth",
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        authNavGraph(navController)

        composable("home") {
            HomeNavHost(
                onLoggedOut = {
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
