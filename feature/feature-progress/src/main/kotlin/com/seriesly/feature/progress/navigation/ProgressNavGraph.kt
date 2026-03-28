package com.seriesly.feature.progress.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.seriesly.core.common.model.ContentType
import com.seriesly.feature.progress.presentation.ratings.MyRatingsScreen

fun NavGraphBuilder.progressNavGraph(navController: NavHostController) {
    composable("progress") {
        MyRatingsScreen(
            onNavigateToDetail = { tvdbId, type ->
                navController.navigate("detail/$tvdbId/${type.name}")
            }
        )
    }
}
