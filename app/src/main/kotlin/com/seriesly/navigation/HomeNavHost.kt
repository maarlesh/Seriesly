package com.seriesly.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.seriesly.feature.detail.navigation.detailNavGraph
import com.seriesly.feature.profile.navigation.profileNavGraph
import com.seriesly.feature.progress.navigation.progressNavGraph
import com.seriesly.feature.search.navigation.searchNavGraph
import com.seriesly.feature.watchlist.navigation.watchlistNavGraph

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem("search",    "Search",    Icons.Outlined.Search,    Icons.Filled.Search),
    BottomNavItem("watchlist", "Watchlist", Icons.Outlined.Bookmarks, Icons.Filled.Bookmarks),
    BottomNavItem("progress",  "Ratings",   Icons.Outlined.Tv,        Icons.Filled.Tv),
    BottomNavItem("profile",   "Profile",   Icons.Outlined.Person,    Icons.Filled.Person),
)

@Composable
fun HomeNavHost(onLoggedOut: () -> Unit = {}) {
    val homeNavController = rememberNavController()
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                    val iconScale by animateFloatAsState(
                        targetValue   = if (selected) 1.15f else 1f,
                        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
                        label         = "navScale_${item.route}"
                    )

                    NavigationBarItem(
                        icon     = {
                            Icon(
                                imageVector        = if (selected) item.selectedIcon else item.icon,
                                contentDescription = item.label,
                                modifier           = Modifier.scale(iconScale)
                            )
                        },
                        label    = { Text(item.label) },
                        selected = selected,
                        onClick  = {
                            homeNavController.navigate(item.route) {
                                popUpTo(homeNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = homeNavController,
            startDestination = "search",
            modifier         = Modifier.padding(innerPadding),
            enterTransition     = { fadeIn(tween(250)) + slideInHorizontally(tween(250)) { it / 4 } },
            exitTransition      = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 4 } },
            popEnterTransition  = { fadeIn(tween(250)) + slideInHorizontally(tween(250)) { -it / 4 } },
            popExitTransition   = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { it / 4 } }
        ) {
            searchNavGraph(homeNavController)
            watchlistNavGraph(homeNavController)
            progressNavGraph(homeNavController)
            profileNavGraph(homeNavController, onLoggedOut)
            detailNavGraph(homeNavController)
        }
    }
}
