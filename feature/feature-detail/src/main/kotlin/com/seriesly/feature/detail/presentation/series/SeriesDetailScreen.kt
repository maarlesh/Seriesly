package com.seriesly.feature.detail.presentation.series

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material3.*
import com.seriesly.core.domain.model.SeriesWatchStatus
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seriesly.core.ui.component.AddToWatchlistSheet
import com.seriesly.core.ui.component.ErrorState
import com.seriesly.core.ui.component.HeroSkeleton
import com.seriesly.core.ui.component.ParticleBurstOverlay
import com.seriesly.core.ui.component.PosterImage
import com.seriesly.core.ui.component.RatingBottomSheet
import com.seriesly.core.ui.tokens.ContentSize
import com.seriesly.feature.detail.presentation.series.component.SeasonSection
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDetailScreen(
    tvdbId: Int,
    onBack: () -> Unit,
    viewModel: SeriesDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                SeriesDetailEvent.NavigateBack -> onBack()
            }
        }
    }

    // Auto-dismiss celebration banner after 2500ms
    LaunchedEffect(uiState.showSeriesCompletedCelebration) {
        if (uiState.showSeriesCompletedCelebration) {
            delay(2500)
            viewModel.onIntent(SeriesDetailIntent.DismissSeriesCompletedCelebration)
        }
    }

    val isCompleted = uiState.progress?.status == SeriesWatchStatus.WATCHED

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(uiState.series?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val bookmarkTint by animateColorAsState(
                        targetValue   = if (uiState.inWatchlistIds.isNotEmpty())
                                            MaterialTheme.colorScheme.primary
                                        else LocalContentColor.current,
                        animationSpec = tween(250),
                        label         = "bookmarkTint"
                    )
                    IconButton(onClick = { viewModel.onIntent(SeriesDetailIntent.ShowWatchlistSheet) }) {
                        Icon(
                            imageVector        = if (uiState.inWatchlistIds.isNotEmpty()) Icons.Filled.Bookmark
                                                 else Icons.Outlined.BookmarkAdd,
                            contentDescription = "Add to watchlist",
                            tint               = bookmarkTint
                        )
                    }
                    val doneAllTint by animateColorAsState(
                        targetValue   = if (isCompleted) MaterialTheme.colorScheme.primary
                                        else LocalContentColor.current,
                        animationSpec = tween(300),
                        label         = "doneAllTint"
                    )
                    IconButton(onClick = { viewModel.onIntent(SeriesDetailIntent.MarkAllWatched) }) {
                        Icon(
                            imageVector        = Icons.Outlined.DoneAll,
                            contentDescription = "Mark all watched",
                            tint               = doneAllTint
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            when {
                uiState.isLoading && uiState.series == null ->
                    HeroSkeleton(modifier = Modifier.fillMaxSize().padding(padding))

                uiState.error != null && uiState.series == null ->
                    ErrorState(
                        error    = uiState.error!!,
                        onRetry  = {},
                        modifier = Modifier.fillMaxSize().padding(padding)
                    )

                uiState.series != null -> {
                    val series = uiState.series!!

                    val animatedProgress by animateFloatAsState(
                        targetValue   = uiState.progress?.progressPercent ?: 0f,
                        animationSpec = spring(dampingRatio = 0.7f, stiffness = 180f),
                        label         = "overallProgress"
                    )

                    LazyColumn(contentPadding = padding) {
                        item {
                            Box(Modifier.fillMaxWidth().height(250.dp)) {
                                PosterImage(
                                    url                = series.backdropUrl,
                                    contentDescription = series.title,
                                    modifier           = Modifier.fillMaxSize(),
                                    width              = 600.dp,
                                    height             = 250.dp
                                )
                                // Gradient scrim over backdrop bottom
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    MaterialTheme.colorScheme.background
                                                )
                                            )
                                        )
                                )
                                PosterImage(
                                    url                = series.posterUrl,
                                    contentDescription = series.title,
                                    modifier           = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp),
                                    width              = ContentSize.posterDetailWidth,
                                    height             = ContentSize.posterDetailHeight,
                                    withShadow         = true
                                )
                            }
                        }
                        item {
                            Column(Modifier.padding(16.dp)) {
                                Text(series.title, style = MaterialTheme.typography.headlineMedium)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    series.firstAired?.let {
                                        Text(it.take(4), style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text(
                                        series.status.name.lowercase().replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                uiState.progress?.takeIf { it.totalAiredEpisodes > 0 }?.let { progress ->
                                    Spacer(Modifier.height(8.dp))
                                    AnimatedContent(
                                        targetState = "${progress.watchedEpisodes} / ${progress.totalAiredEpisodes} aired episodes watched",
                                        label       = "progressText"
                                    ) { text ->
                                        Text(
                                            text  = text,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { animatedProgress },
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 12.dp)
                                ) {
                                    AssistChip(
                                        onClick = { viewModel.onIntent(SeriesDetailIntent.ShowRatingSheet) },
                                        label   = {
                                            Text(
                                                if (uiState.userRating != null)
                                                    "\u2605 ${"%.1f".format(uiState.userRating)}"
                                                else "Rate"
                                            )
                                        }
                                    )
                                }
                                if (series.genres.isNotEmpty()) {
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text  = series.genres.joinToString(" · "),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(series.overview, style = MaterialTheme.typography.bodyMedium)
                            }
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

            // Series Complete! banner — wrapped in Column to provide ColumnScope for AnimatedVisibility
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = padding.calculateTopPadding() + 8.dp)
                    .padding(horizontal = 16.dp)
            ) {
                AnimatedVisibility(
                    visible = uiState.showSeriesCompletedCelebration,
                    enter   = slideInVertically { -it } + fadeIn(),
                    exit    = fadeOut()
                ) {
                    Surface(
                        shape           = MaterialTheme.shapes.medium,
                        color           = MaterialTheme.colorScheme.primaryContainer,
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
                                tint               = MaterialTheme.colorScheme.primary,
                                modifier           = Modifier.size(20.dp)
                            )
                            Text(
                                text  = "Series Complete!",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Particle burst overlay
            if (uiState.showSeriesCompletedCelebration) {
                ParticleBurstOverlay(
                    onComplete = { /* Banner auto-dismisses via LaunchedEffect */ }
                )
            }
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
