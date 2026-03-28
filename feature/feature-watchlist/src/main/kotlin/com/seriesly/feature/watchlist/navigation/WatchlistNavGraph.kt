package com.seriesly.feature.watchlist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.seriesly.core.common.model.ContentType
import com.seriesly.feature.watchlist.presentation.detail.WatchlistDetailScreen
import com.seriesly.feature.watchlist.presentation.list.WatchlistListScreen

fun NavGraphBuilder.watchlistNavGraph(navController: NavHostController) {
    navigation(startDestination = "watchlist_list", route = "watchlist") {
        composable("watchlist_list") {
            WatchlistListScreen(
                onNavigateToDetail = { navController.navigate("watchlist_detail/$it") }
            )
        }
        composable(
            route = "watchlist_detail/{watchlistId}",
            arguments = listOf(navArgument("watchlistId") { type = NavType.LongType })
        ) {
            WatchlistDetailScreen(
                onNavigateToContent = { tvdbId, type ->
                    navController.navigate("detail/$tvdbId/${type.name}")
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
