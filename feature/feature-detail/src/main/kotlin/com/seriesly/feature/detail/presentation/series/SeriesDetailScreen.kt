package com.seriesly.feature.detail.presentation.series

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.seriesly.core.domain.model.SeriesWatchStatus
import com.seriesly.core.ui.component.AddToWatchlistSheet
import com.seriesly.core.ui.component.ErrorState
import com.seriesly.core.ui.component.HeroSkeleton
import com.seriesly.core.ui.component.ParticleBurstOverlay
import com.seriesly.core.ui.component.RatingBottomSheet
import com.seriesly.core.ui.theme.*
import com.seriesly.core.ui.tokens.Brushes
import com.seriesly.core.ui.tokens.Motion
import com.seriesly.feature.detail.presentation.series.component.SeasonSection
import kotlinx.coroutines.delay

@Composable
fun SeriesDetailScreen(
    tvdbId: Int,
    onBack: () -> Unit,
    viewModel: SeriesDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()

    // Dynamic alpha for top bar based on scroll
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
                SeriesDetailEvent.NavigateBack -> onBack()
            }
        }
    }

    LaunchedEffect(uiState.showSeriesCompletedCelebration) {
        if (uiState.showSeriesCompletedCelebration) {
            delay(2500)
            viewModel.onIntent(SeriesDetailIntent.DismissSeriesCompletedCelebration)
        }
    }

    val isCompleted = uiState.progress?.status == SeriesWatchStatus.WATCHED

    // Charging animation offset (0→1 loops)
    val infiniteTransition = rememberInfiniteTransition(label = "charging")
    val chargeOffset by infiniteTransition.animateFloat(
        initialValue   = 0f,
        targetValue    = 1f,
        animationSpec  = Motion.ChargingInfinite,
        label          = "chargeOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        val series = uiState.series
        if (series != null) {
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
                    model              = series.backdropUrl ?: series.posterUrl,
                    contentDescription = null,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize()
                )
                // Gradient overlay
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
            uiState.isLoading && uiState.series == null -> {
                HeroSkeleton(modifier = Modifier.fillMaxSize())
            }

            uiState.error != null && uiState.series == null -> {
                ErrorState(
                    error    = uiState.error!!,
                    onRetry  = {},
                    modifier = Modifier.fillMaxSize()
                )
            }

            uiState.series != null -> {
                val series = uiState.series!!

                val animatedProgress by animateFloatAsState(
                    targetValue   = uiState.progress?.progressPercent ?: 0f,
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = 180f),
                    label         = "overallProgress"
                )
                val watchedCount  = uiState.progress?.watchedEpisodes ?: 0
                val totalCount    = uiState.progress?.totalAiredEpisodes ?: 0
                val progressPct   = if (totalCount > 0) (watchedCount * 100 / totalCount) else 0

                LazyColumn(
                    state          = scrollState,
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // ── Hero Spacer ──────────────────────────────────────────
                    item {
                        Spacer(Modifier.height(420.dp))
                    }

                    // ── Metadata Row ──────────────────────────────────────────
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
                                        // Soft shadow
                                        drawRect(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                            )
                                        )
                                    }
                            ) {
                                AsyncImage(
                                    model              = series.posterUrl,
                                    contentDescription = series.title,
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
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(Secondary.copy(alpha = 0.15f))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text  = series.status.name.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Secondary
                                    )
                                }
                                Text(
                                    text     = series.title,
                                    style    = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color    = OnSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    series.firstAired?.let { aired ->
                                        Text(
                                            text  = "First Aired: ${aired.take(10)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSurfaceVariant
                                        )
                                    }
                                    series.tvdbRating?.let { rating ->
                                        Row(
                                            verticalAlignment     = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector        = Icons.Filled.CheckCircle,
                                                contentDescription = null,
                                                tint               = Tertiary,
                                                modifier           = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text  = "%.1f".format(rating),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = OnSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Progress & Action Card (Glass) ────────────────────────
                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceContainerLow.copy(alpha = 0.85f))
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Progress bar header
                            if (totalCount > 0) {
                                Row(
                                    modifier              = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text  = "SERIES PROGRESS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OnSurfaceVariant
                                    )
                                    Text(
                                        text  = "$progressPct%",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                        color = Secondary
                                    )
                                }

                                // Charging progress bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SurfaceContainerHighest.copy(alpha = 0.5f))
                                        .padding(2.dp)
                                ) {
                                    val fillFraction = animatedProgress.coerceIn(0f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(fillFraction)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                // Charging sweep: shift gradient by offset
                                                Brush.horizontalGradient(
                                                    colorStops = arrayOf(
                                                        ((chargeOffset - 0.5f).coerceAtLeast(0f)) to Secondary,
                                                        chargeOffset to SecondaryContainer,
                                                        ((chargeOffset + 0.5f).coerceAtMost(1f)) to Secondary
                                                    )
                                                )
                                            )
                                            .drawBehind {
                                                // Green glow
                                                drawRect(
                                                    Brush.horizontalGradient(
                                                        colors = listOf(
                                                            Secondary.copy(alpha = 0f),
                                                            Secondary.copy(alpha = 0.5f)
                                                        )
                                                    )
                                                )
                                            }
                                    )
                                }

                                Text(
                                    text  = "$watchedCount of $totalCount episodes watched".uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Outline
                                )
                            }

                            // Rating chip
                            AssistChip(
                                onClick = { viewModel.onIntent(SeriesDetailIntent.ShowRatingSheet) },
                                label   = {
                                    Text(
                                        if (uiState.userRating != null)
                                            "★ ${"%.1f".format(uiState.userRating)}"
                                        else "Rate this series"
                                    )
                                },
                                colors  = AssistChipDefaults.assistChipColors(
                                    containerColor    = SurfaceContainerHigh.copy(alpha = 0.6f),
                                    labelColor        = if (uiState.userRating != null) Tertiary else OnSurfaceVariant
                                )
                            )

                            // Action buttons
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Watchlist glass button
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Primary.copy(alpha = 0.20f))
                                        .clickable { viewModel.onIntent(SeriesDetailIntent.ShowWatchlistSheet) }
                                        .padding(vertical = 14.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector        = if (uiState.inWatchlistIds.isNotEmpty())
                                                                 Icons.Filled.BookmarkAdded
                                                             else Icons.Outlined.BookmarkAdd,
                                        contentDescription = "Watchlist",
                                        tint               = Primary,
                                        modifier           = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text  = "Watchlist",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Primary
                                    )
                                }
                                // All Watched glass button
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .clickable { viewModel.onIntent(SeriesDetailIntent.MarkAllWatched) }
                                        .padding(vertical = 14.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector        = Icons.Outlined.DoneAll,
                                        contentDescription = "Mark all watched",
                                        tint               = if (isCompleted) Secondary else OnSurface,
                                        modifier           = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text  = "All Watched",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isCompleted) Secondary else OnSurface
                                    )
                                }
                            }
                        }
                    }

                    // ── Genres & Overview (Glass) ─────────────────────────────
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
                            if (series.genres.isNotEmpty()) {
                                Text(
                                    text  = "GENRE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Outline
                                )
                                Text(
                                    text  = series.genres.joinToString(" · "),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Primary
                                )
                            }
                            if (series.overview.isNotBlank()) {
                                Text(
                                    text  = "SYNOPSIS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Outline
                                )
                                Text(
                                    text  = series.overview,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }

                    // ── Seasons ───────────────────────────────────────────────
                    item {
                        Text(
                            text     = "Seasons",
                            style    = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color    = Primary,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                    items(series.seasons) { season ->
                        SeasonSection(
                            season              = season,
                            expandedSeasonIds   = uiState.expandedSeasonIds,
                            episodeWatched      = uiState.episodeWatched,
                            onToggleExpand      = { viewModel.onIntent(SeriesDetailIntent.ToggleSeason(it)) },
                            onEpisodeToggle     = { episode, watched ->
                                viewModel.onIntent(SeriesDetailIntent.EpisodeToggled(episode.episodeId, watched))
                            },
                            onMarkSeasonWatched = { viewModel.onIntent(SeriesDetailIntent.SeasonMarkWatched(it)) }
                        )
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = Primary)
                }
                Text(
                    text  = "Seriesly",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Primary
                )
            }
            Row {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Search, "Search", tint = Primary)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.MoreVert, "More", tint = Primary)
                }
            }
        }

        // Series Complete! banner
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp)
                .padding(horizontal = 16.dp)
        ) {
            AnimatedVisibility(
                visible = uiState.showSeriesCompletedCelebration,
                enter   = slideInVertically { -it } + fadeIn(),
                exit    = fadeOut()
            ) {
                Surface(
                    shape           = MaterialTheme.shapes.medium,
                    color           = SecondaryContainer,
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier              = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint               = Secondary,
                            modifier           = Modifier.size(20.dp)
                        )
                        Text(
                            text  = "Series Complete!",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSecondaryContainer
                        )
                    }
                }
            }
        }

        if (uiState.showSeriesCompletedCelebration) {
            ParticleBurstOverlay(onComplete = {})
        }
    }

    if (uiState.showWatchlistSheet) {
        AddToWatchlistSheet(
            watchlists         = uiState.watchlists,
            alreadyInIds       = uiState.inWatchlistIds,
            onWatchlistToggled = { id, add -> viewModel.onIntent(SeriesDetailIntent.ToggleWatchlist(id, add)) },
            onDismiss          = { viewModel.onIntent(SeriesDetailIntent.DismissWatchlistSheet) }
        )
    }

    if (uiState.showRatingPrompt) {
        RatingBottomSheet(
            title         = uiState.series?.title ?: "",
            initialRating = uiState.userRating ?: 0f,
            onDismiss     = { viewModel.onIntent(SeriesDetailIntent.DismissRatingSheet) },
            onSave        = { rating, comment -> viewModel.onIntent(SeriesDetailIntent.SaveRating(rating, comment)) }
        )
    }
}
