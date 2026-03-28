package com.seriesly.feature.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.seriesly.feature.auth.presentation.login.LoginScreen
import com.seriesly.feature.auth.presentation.register.RegisterScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(startDestination = "login", route = "auth") {
        composable("login") {
            LoginScreen(
                onNavigateToHome     = { navController.navigate("home") { popUpTo("auth") { inclusive = true } } },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToHome  = { navController.navigate("home") { popUpTo("auth") { inclusive = true } } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
    }
}
