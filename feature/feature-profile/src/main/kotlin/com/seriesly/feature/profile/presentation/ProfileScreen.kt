package com.seriesly.feature.profile.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seriesly.core.ui.theme.BrandPurple
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMyRatings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ProfileEvent.NavigateToLogin     -> onNavigateToLogin()
                ProfileEvent.NavigateToMyRatings -> onNavigateToMyRatings()
            }
        }
    }

    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(ProfileIntent.LogoutDismissed) },
            title            = { Text("Sign Out") },
            text             = { Text("Are you sure you want to sign out? Your data will be saved.") },
            confirmButton    = {
                TextButton(onClick = { viewModel.onIntent(ProfileIntent.LogoutConfirmed) }) {
                    Text("Sign Out", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton    = {
                TextButton(onClick = { viewModel.onIntent(ProfileIntent.LogoutDismissed) }) {
                    Text("Cancel")
                }
            }
        )
    }

    var launched by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { launched = true }

    val avatarScale by animateFloatAsState(
        targetValue   = if (launched) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label         = "avatarScale"
    )

    // Avatar pulsing border
    val ringAlpha by rememberInfiniteTransition(label = "avatarPulse").animateFloat(
        initialValue  = 0.4f,
        targetValue   = 0.9f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringAlpha"
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile") }) }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val stats = uiState.stats ?: return@Scaffold

        LazyColumn(contentPadding = padding) {
            item {
                Column(
                    modifier            = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsing avatar
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .scale(avatarScale)
                            .border(3.dp, BrandPurple.copy(alpha = ringAlpha), CircleShape)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = stats.username.first().uppercase(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    AnimatedVisibility(
                        visible = launched,
                        enter   = slideInVertically(tween(300, delayMillis = 150)) { 20 } + fadeIn(tween(300, delayMillis = 150))
                    ) {
                        Text(stats.username, style = MaterialTheme.typography.titleLarge)
                    }

                    AnimatedVisibility(
                        visible = launched,
                        enter   = fadeIn(tween(300, delayMillis = 200))
                    ) {
                        Text(
                            text  = "Member since ${formatDate(stats.memberSince)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                // Column provides ColumnScope for AnimatedVisibility
                Column {
                    AnimatedVisibility(
                        visible = launched,
                        enter   = slideInVertically(tween(300, delayMillis = 250)) { 20 } + fadeIn(tween(300, delayMillis = 250))
                    ) {
                        Row(
                            modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                label       = "Watchlists",
                                targetCount = stats.totalWatchlists,
                                modifier    = Modifier.weight(1f)
                            )
                            StatCard(
                                label       = "Rated",
                                targetCount = stats.totalRatings,
                                modifier    = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            item {
                Column {
                    AnimatedVisibility(
                        visible = launched,
                        enter   = slideInVertically(tween(300, delayMillis = 320)) { 20 } + fadeIn(tween(300, delayMillis = 320))
                    ) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape    = MaterialTheme.shapes.large,
                            onClick  = { viewModel.onIntent(ProfileIntent.MyRatingsClicked) }
                        ) {
                            ListItem(
                                headlineContent = { Text("My Ratings") },
                                leadingContent  = { Icon(Icons.Outlined.Star, contentDescription = null) },
                                trailingContent = {
                                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            }

            item {
                Column {
                    AnimatedVisibility(
                        visible = launched,
                        enter   = slideInVertically(tween(300, delayMillis = 370)) { 20 } + fadeIn(tween(300, delayMillis = 370))
                    ) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape    = MaterialTheme.shapes.large,
                            onClick  = { viewModel.onIntent(ProfileIntent.LogoutClicked) }
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text("Sign Out", color = MaterialTheme.colorScheme.error)
                                },
                                leadingContent  = {
                                    Icon(
                                        imageVector        = Icons.AutoMirrored.Outlined.Logout,
                                        contentDescription = null,
                                        tint               = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatCard(label: String, targetCount: Int, modifier: Modifier = Modifier) {
    var displayCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(targetCount) {
        val step = (targetCount / 20).coerceAtLeast(1)
        while (displayCount < targetCount) {
            delay(40L)
            displayCount = (displayCount + step).coerceAtMost(targetCount)
        }
        displayCount = targetCount
    }

    ElevatedCard(
        modifier  = modifier,
        shape     = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = displayCount.toString(),
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatDate(epochMs: Long): String =
    java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault())
        .format(java.util.Date(epochMs))
