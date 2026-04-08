package com.seriesly.feature.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.seriesly.feature.profile.presentation.ProfileScreen

fun NavGraphBuilder.profileNavGraph(
    navController: NavHostController,
    onLoggedOut: () -> Unit
) {
    navigation(startDestination = "profile_home", route = "profile") {
        composable("profile_home") {
            ProfileScreen(
                onNavigateToLogin        = onLoggedOut,
                onNavigateToMyRatings    = { navController.navigate("progress") },
                onNavigateToWatchHistory = { navController.navigate("history") }
            )
        }
    }
}
