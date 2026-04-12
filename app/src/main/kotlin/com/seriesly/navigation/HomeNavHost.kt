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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.seriesly.core.ui.theme.*
import com.seriesly.feature.detail.navigation.detailNavGraph
import com.seriesly.feature.profile.navigation.profileNavGraph
import com.seriesly.feature.progress.navigation.progressNavGraph
import com.seriesly.feature.search.navigation.searchNavGraph
import com.seriesly.feature.watchlist.navigation.watchlistNavGraph
import kotlin.math.abs

private data class BottomNavItem(
    val route:        String,
    val label:        String,
    val icon:         ImageVector,
    val selectedIcon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem("search",    "Home",      Icons.Outlined.Movie,       Icons.Filled.Movie),
    BottomNavItem("watchlist", "Watchlist", Icons.Outlined.Bookmarks,   Icons.Filled.Bookmarks),
    BottomNavItem("progress",  "Ratings",   Icons.Outlined.StarOutline, Icons.Filled.Star),
    BottomNavItem("profile",   "Profile",   Icons.Outlined.Person,      Icons.Filled.Person),
)

/**
 * Find which bottom-nav tab an entry belongs to by walking its destination hierarchy.
 * This correctly handles nested nav graphs (e.g. "watchlist" graph → "watchlist_list" leaf,
 * "profile" graph → "profile_home" leaf) where destination.route != the tab route string.
 */
private fun tabIndexOf(entry: NavBackStackEntry): Int =
    bottomNavItems.indexOfFirst { item ->
        entry.destination.hierarchy.any { it.route == item.route }
    }

@Composable
fun HomeNavHost(onLoggedOut: () -> Unit = {}) {
    val homeNavController = rememberNavController()
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentTabIndex = bottomNavItems.indexOfFirst { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }

    // Captured before each navigate call so transition lambdas always see the true
    // source tab, even if initialState resolves to an intermediate back-stack entry.
    val fromTabRef = remember { intArrayOf(0) }

    fun navigateToTab(targetIndex: Int) {
        if (currentTabIndex >= 0) fromTabRef[0] = currentTabIndex
        val item = bottomNavItems[targetIndex]
        if (item.route == "search") {
            homeNavController.popBackStack("search", inclusive = false)
        } else {
            homeNavController.navigate(item.route) {
                popUpTo("search") {
                    saveState = true
                }
                launchSingleTop = true
                restoreState    = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        var swipeDeltaX by remember { mutableFloatStateOf(0f) }

        NavHost(
            navController    = homeNavController,
            startDestination = "search",
            modifier         = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 80.dp)
                .pointerInput(currentTabIndex) {
                    detectHorizontalDragGestures(
                        onDragStart = { swipeDeltaX = 0f },
                        onDragEnd   = {
                            val threshold = 80.dp.toPx()
                            if (currentTabIndex >= 0 && abs(swipeDeltaX) > threshold) {
                                val direction = if (swipeDeltaX < 0) 1 else -1
                                val next = (currentTabIndex + direction)
                                    .coerceIn(0, bottomNavItems.lastIndex)
                                if (next != currentTabIndex) navigateToTab(next)
                            }
                            swipeDeltaX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount -> swipeDeltaX += dragAmount }
                    )
                },
            enterTransition = {
                // Walk hierarchy to find tab index — handles nested nav graphs correctly.
                val toIdx   = tabIndexOf(targetState)
                val fromIdx = tabIndexOf(initialState)
                    .let { if (it >= 0) it else fromTabRef[0] }  // fallback: last known source tab
                val fromRight = if (toIdx >= 0) toIdx > fromIdx else true
                fadeIn(tween(250)) + slideInHorizontally(tween(250)) { if (fromRight) it / 4 else -it / 4 }
            },
            exitTransition = {
                val toIdx   = tabIndexOf(targetState)
                val fromIdx = tabIndexOf(initialState)
                    .let { if (it >= 0) it else fromTabRef[0] }
                val exitRight = if (toIdx >= 0) toIdx < fromIdx else false
                fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { if (exitRight) it / 4 else -it / 4 }
            },
            popEnterTransition = {
                // Popping back from a detail screen → enter from left.
                fadeIn(tween(250)) + slideInHorizontally(tween(250)) { -it / 4 }
            },
            popExitTransition = {
                // Popping back from a detail screen → exit to right.
                fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { it / 4 }
            }
        ) {
            searchNavGraph(homeNavController)
            watchlistNavGraph(homeNavController)
            progressNavGraph(homeNavController)
            profileNavGraph(homeNavController, onLoggedOut)
            detailNavGraph(homeNavController)
        }

        // Status bar background
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
            bottomNavItems.forEachIndexed { index, item ->
                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                val iconScale by animateFloatAsState(
                    targetValue   = if (selected) 1.10f else 1f,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
                    label         = "navScale_${item.route}"
                )

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { navigateToTab(index) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (selected) 6.dp else 0.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) Secondary else Color.Transparent)
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
