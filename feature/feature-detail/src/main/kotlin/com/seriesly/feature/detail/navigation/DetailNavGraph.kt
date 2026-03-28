package com.seriesly.feature.detail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.seriesly.core.common.model.ContentType
import com.seriesly.feature.detail.presentation.movie.MovieDetailScreen
import com.seriesly.feature.detail.presentation.series.SeriesDetailScreen

fun NavGraphBuilder.detailNavGraph(navController: NavHostController) {
    composable(
        route = "detail/{tvdbId}/{contentType}",
        arguments = listOf(
            navArgument("tvdbId") { type = NavType.IntType },
            navArgument("contentType") { type = NavType.StringType }
        )
    ) { backStack ->
        val tvdbId = backStack.arguments?.getInt("tvdbId") ?: return@composable
        val type   = runCatching {
            ContentType.valueOf(backStack.arguments?.getString("contentType") ?: "MOVIE")
        }.getOrDefault(ContentType.MOVIE)

        if (type == ContentType.MOVIE) {
            MovieDetailScreen(tvdbId = tvdbId, onBack = { navController.popBackStack() })
        } else {
            SeriesDetailScreen(tvdbId = tvdbId, onBack = { navController.popBackStack() })
        }
    }
}
