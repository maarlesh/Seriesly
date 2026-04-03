package com.seriesly.feature.search.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.seriesly.core.common.model.ContentType
import com.seriesly.feature.search.presentation.SearchScreen

fun NavGraphBuilder.searchNavGraph(navController: NavHostController) {
    composable("search") {
        SearchScreen(
            onNavigateToDetail    = { tvdbId, type -> navController.navigate("detail/$tvdbId/${type.name}") },
            onNavigateToWatchlist = { watchlistId  -> navController.navigate("watchlist_detail/$watchlistId") }
        )
    }
}
