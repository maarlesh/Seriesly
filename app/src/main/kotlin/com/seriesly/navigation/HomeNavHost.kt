package com.seriesly.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.seriesly.core.ui.theme.*
import com.seriesly.feature.detail.navigation.detailNavGraph
import com.seriesly.feature.profile.navigation.profileNavGraph
import com.seriesly.feature.progress.navigation.progressNavGraph
import com.seriesly.feature.search.navigation.searchNavGraph
import com.seriesly.feature.watchlist.navigation.watchlistNavGraph

private data class BottomNavItem(
    val route:        String,
    val label:        String,
    val icon:         ImageVector,
    val selectedIcon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem("search",    "Home",      Icons.Outlined.Movie,     Icons.Filled.Movie),
    BottomNavItem("watchlist", "Watchlist", Icons.Outlined.Bookmarks, Icons.Filled.Bookmarks),
    BottomNavItem("progress",  "Ratings",   Icons.Outlined.StarOutline, Icons.Filled.Star),
    BottomNavItem("profile",   "Profile",   Icons.Outlined.Person,    Icons.Filled.Person),
)

@Composable
fun HomeNavHost(onLoggedOut: () -> Unit = {}) {
    val homeNavController = rememberNavController()
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        NavHost(
            navController    = homeNavController,
            startDestination = "search",
            modifier         = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 80.dp),  // leave room for glass nav bar
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

        // Status bar background — prevents content from bleeding into the system status bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(Background)
                .align(Alignment.TopStart)
        )

        // Glass bottom navigation bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Background.copy(alpha = 0.80f))
                .navigationBarsPadding()
                .height(72.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                val iconScale by animateFloatAsState(
                    targetValue   = if (selected) 1.10f else 1f,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
                    label         = "navScale_${item.route}"
                )

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            homeNavController.navigate(item.route) {
                                popUpTo(homeNavController.graph.findStartDestination().id) {
                                    saveState = true
                                    inclusive = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Active dot indicator (above icon)
                    Box(
                        modifier = Modifier
                            .size(if (selected) 6.dp else 0.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (selected) Secondary else Color.Transparent
                            )
                    )

                    Icon(
                        imageVector        = if (selected) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        tint               = if (selected) Secondary else Outline,
                        modifier           = Modifier
                            .size(24.dp)
                            .scale(iconScale)
                    )

                    Text(
                        text  = item.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (selected) FontWeight.Black else FontWeight.Bold
                        ),
                        color = if (selected) Secondary else Outline
                    )
                }
            }
        }
    }
}
