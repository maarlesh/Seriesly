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
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seriesly.core.ui.theme.*
import kotlinx.coroutines.delay

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
                    Text("Sign Out", color = Error)
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

    val ringAlpha by rememberInfiniteTransition(label = "avatarPulse").animateFloat(
        initialValue  = 0.4f,
        targetValue   = 0.9f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            return@Box
        }

        val stats = uiState.stats ?: return@Box

        // ── "Restoring your data…" banner (first-time pull only) ─────────────
        AnimatedVisibility(
            visible = uiState.isSyncing,
            enter   = fadeIn(tween(300)),
            exit    = fadeOut(tween(300)),
            modifier = Modifier.align(Alignment.TopCenter).zIndex(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(
                    modifier  = Modifier.size(16.dp),
                    color     = Primary,
                    strokeWidth = 2.dp
                )
                Text(
                    text  = "Restoring your data…",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        }

        LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {

            // ── Editorial header ──────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Outlined.Person,
                            contentDescription = null,
                            tint               = Secondary,
                            modifier           = Modifier.size(14.dp)
                        )
                        Text(
                            text  = "CINEPHILE IDENTITY",
                            style = MaterialTheme.typography.labelSmall,
                            color = Outline
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text  = "Your Profile",
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = OnSurface
                    )
                }
            }

            // ── Avatar + username ─────────────────────────────────────────
            item {
                Column(
                    modifier            = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .scale(avatarScale)
                            .border(3.dp, Primary.copy(alpha = ringAlpha), CircleShape)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = stats.username.first().uppercase(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = OnPrimary
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    AnimatedVisibility(
                        visible = launched,
                        enter   = slideInVertically(tween(300, delayMillis = 150)) { 20 } + fadeIn(tween(300, delayMillis = 150))
                    ) {
                        Text(
                            text  = stats.username,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface
                        )
                    }

                    AnimatedVisibility(
                        visible = launched,
                        enter   = fadeIn(tween(300, delayMillis = 200))
                    ) {
                        Text(
                            text  = "Member since ${formatDate(stats.memberSince)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            // ── Stats row ─────────────────────────────────────────────────
            item {
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

            // ── My Ratings row ────────────────────────────────────────────
            item {
                Column {
                    AnimatedVisibility(
                        visible = launched,
                        enter   = slideInVertically(tween(300, delayMillis = 320)) { 20 } + fadeIn(tween(300, delayMillis = 320))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceContainerLow)
                                .clickable { viewModel.onIntent(ProfileIntent.MyRatingsClicked) }
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Outlined.Star, contentDescription = null, tint = Tertiary)
                                Text(
                                    text  = "My Ratings",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = OnSurface
                                )
                            }
                            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, tint = Outline)
                        }
                    }
                }
            }

            // ── Privacy policy row ────────────────────────────────────────
            item {
                val uriHandler = LocalUriHandler.current
                Column {
                    AnimatedVisibility(
                        visible = launched,
                        enter   = slideInVertically(tween(300, delayMillis = 345)) { 20 } + fadeIn(tween(300, delayMillis = 345))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceContainerLow)
                                .clickable { uriHandler.openUri("https://maarlesh.github.io/Seriesly/privacy-policy") }
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Outlined.PrivacyTip, contentDescription = null, tint = Secondary)
                                Text(
                                    text  = "Privacy Policy",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = OnSurface
                                )
                            }
                            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, tint = Outline)
                        }
                    }
                }
            }

            // ── Sign out row ──────────────────────────────────────────────
            item {
                Column {
                    AnimatedVisibility(
                        visible = launched,
                        enter   = slideInVertically(tween(300, delayMillis = 370)) { 20 } + fadeIn(tween(300, delayMillis = 370))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceContainerLow)
                                .clickable { viewModel.onIntent(ProfileIntent.LogoutClicked) }
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Outlined.Logout, null, tint = Error)
                            Text(
                                text  = "Sign Out",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Error
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

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text  = displayCount.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Primary
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.labelMedium,
                color = OnSurfaceVariant
            )
        }
    }
}

private fun formatDate(epochMs: Long): String =
    java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault())
        .format(java.util.Date(epochMs))
