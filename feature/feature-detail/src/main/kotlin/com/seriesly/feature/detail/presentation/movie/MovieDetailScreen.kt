package com.seriesly.feature.detail.presentation.movie

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.seriesly.core.ui.component.AddToWatchlistSheet
import com.seriesly.core.ui.component.ErrorState
import com.seriesly.core.ui.component.HeroSkeleton
import com.seriesly.core.ui.component.ParticleBurstOverlay
import com.seriesly.core.ui.component.RatingBottomSheet
import com.seriesly.core.ui.theme.*
import com.seriesly.core.ui.tokens.Brushes

@Composable
fun MovieDetailScreen(
    tvdbId: Int,
    onBack: () -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()
    
    // Dynamic alpha for top bar and blur based on scroll
    val headerHeightPx = with(LocalDensity.current) { 400.dp.toPx() }
    val toolbarAlpha by remember {
        derivedStateOf {
            val scrollOffset = scrollState.firstVisibleItemScrollOffset.toFloat()
            val firstVisibleIndex = scrollState.firstVisibleItemIndex
            
            if (firstVisibleIndex > 0) 1f
            else (scrollOffset / headerHeightPx).coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                MovieDetailEvent.NavigateBack -> onBack()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        val movie = uiState.movie
        if (movie != null) {
            // Cinematic Fixed Background Image with Blur & Parallax
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val scrollOffset = scrollState.firstVisibleItemScrollOffset.toFloat()
                        val firstVisibleIndex = scrollState.firstVisibleItemIndex
                        translationY = if (firstVisibleIndex == 0) -scrollOffset * 0.4f else -headerHeightPx * 0.4f
                    }
                    .blur(radius = (toolbarAlpha * 25).dp) // Strong blur as user scrolls up
            ) {
                AsyncImage(
                    model              = movie.backdropUrl ?: movie.posterUrl,
                    contentDescription = null,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
                // Gradient overlay to keep the bottom dark and text readable
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.0f to Color.Transparent,
                                    0.5f to Background.copy(alpha = 0.4f),
                                    1.0f to Background.copy(alpha = 0.9f)
                                )
                            )
                        )
                )
            }
        }

        when {
            uiState.isLoading && uiState.movie == null ->
                HeroSkeleton(modifier = Modifier.fillMaxSize())

            uiState.error != null && uiState.movie == null ->
                ErrorState(
                    error    = uiState.error!!,
                    onRetry  = {},
                    modifier = Modifier.fillMaxSize()
                )

            uiState.movie != null -> {
                val movie = uiState.movie!!

                LazyColumn(
                    state          = scrollState,
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {

                    // ── Hero Spacer (allows background to show) ──────────────
                    item {
                        Spacer(Modifier.height(420.dp))
                    }

                    // ── Metadata Row ─────────────────────────────────────────
                    item {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .padding(bottom = 24.dp),
                            verticalAlignment     = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Poster card
                            Box(
                                modifier = Modifier
                                    .width(104.dp)
                                    .aspectRatio(2f / 3f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainerHighest)
                                    .drawBehind {
                                        drawRect(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                            )
                                        )
                                    }
                            ) {
                                AsyncImage(
                                    model              = movie.posterUrl,
                                    contentDescription = movie.title,
                                    contentScale       = ContentScale.Crop,
                                    modifier           = Modifier.fillMaxSize()
                                )
                            }

                            // Metadata
                            Column(
                                modifier = Modifier.padding(bottom = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Status badge
                                movie.status?.let { status ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(Primary.copy(alpha = 0.15f))
                                            .padding(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text  = status.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Primary
                                        )
                                    }
                                }
                                Text(
                                    text     = movie.title,
                                    style    = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color    = OnSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    movie.year?.let {
                                        Text(
                                            text  = "$it",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSurfaceVariant
                                        )
                                    }
                                    movie.runtimeMinutes?.let {
                                        Text(
                                            text  = "${it}m",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── Action Card (Semi-transparent Glass) ─────────────────
                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceContainerLow.copy(alpha = 0.85f))
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                WatchedChip(
                                    isWatched = uiState.isWatched,
                                    onClick   = { viewModel.onIntent(MovieDetailIntent.ToggleWatched) }
                                )
                                AssistChip(
                                    onClick = { viewModel.onIntent(MovieDetailIntent.ShowRatingSheet) },
                                    label   = {
                                        Text(
                                            if (uiState.userRating != null)
                                                "★ ${"%.1f".format(uiState.userRating)}"
                                            else "Rate"
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = SurfaceContainerHigh.copy(alpha = 0.6f),
                                        labelColor     = if (uiState.userRating != null) Tertiary else OnSurfaceVariant
                                    )
                                )
                                // Watchlist button
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Primary.copy(alpha = 0.20f))
                                        .clickable { viewModel.onIntent(MovieDetailIntent.ShowWatchlistSheet) }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector        = if (uiState.inWatchlistIds.isNotEmpty())
                                                                Icons.Filled.Bookmark
                                                            else Icons.Outlined.BookmarkAdd,
                                        contentDescription = "Watchlist",
                                        tint               = Primary,
                                        modifier           = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // ── Genres & Overview (Semi-transparent Glass) ───────────
                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceContainerLow.copy(alpha = 0.85f))
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (movie.genres.isNotEmpty()) {
                                Text(
                                    text  = "GENRE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Outline
                                )
                                Text(
                                    text  = movie.genres.joinToString(" · "),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Primary
                                )
                            }
                            if (movie.overview.isNotBlank()) {
                                Text(
                                    text  = "SYNOPSIS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Outline
                                )
                                Text(
                                    text  = movie.overview,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Glass top app bar (floating)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .background(Surface.copy(alpha = toolbarAlpha))
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = Primary)
            }
            Text(
                text  = "Seriesly",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = Primary
            )
            // spacer to balance layout
            Box(Modifier.size(48.dp))
        }
    }

    if (uiState.showWatchedCelebration) {
        ParticleBurstOverlay(onComplete = { viewModel.onIntent(MovieDetailIntent.DismissWatchedCelebration) })
    }

    if (uiState.showWatchlistSheet) {
        AddToWatchlistSheet(
            watchlists         = uiState.watchlists,
            alreadyInIds       = uiState.inWatchlistIds,
            onWatchlistToggled = { id, add -> viewModel.onIntent(MovieDetailIntent.ToggleWatchlist(id, add)) },
            onDismiss          = { viewModel.onIntent(MovieDetailIntent.DismissWatchlistSheet) }
        )
    }

    if (uiState.showRatingSheet) {
        RatingBottomSheet(
            title          = uiState.movie?.title ?: "",
            initialRating  = uiState.userRating ?: 0f,
            initialComment = uiState.userComment ?: "",
            onDismiss      = { viewModel.onIntent(MovieDetailIntent.DismissRatingSheet) },
            onSave         = { rating, comment -> viewModel.onIntent(MovieDetailIntent.SaveRating(rating, comment)) }
        )
    }
}

@Composable
private fun WatchedChip(isWatched: Boolean, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val bgColor by animateColorAsState(
        targetValue   = if (isWatched) MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        animationSpec = tween(250),
        label         = "chipBg"
    )
    val textColor by animateColorAsState(
        targetValue   = if (isWatched) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(250),
        label         = "chipText"
    )
    val iconScale by animateFloatAsState(
        targetValue   = if (isWatched) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 600f),
        label         = "chipIcon"
    )

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(bgColor)
            .clickable {
                if (!isWatched) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (isWatched || iconScale > 0f) {
            Icon(
                imageVector        = Icons.Filled.Check,
                contentDescription = null,
                tint               = textColor,
                modifier           = Modifier.size(16.dp).scale(iconScale)
            )
        }
        Text(
            text  = if (isWatched) "Watched" else "Mark Watched",
            color = textColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
