package com.seriesly.feature.detail.presentation.movie

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seriesly.core.ui.component.AddToWatchlistSheet
import com.seriesly.core.ui.component.ErrorState
import com.seriesly.core.ui.component.HeroSkeleton
import com.seriesly.core.ui.component.PosterImage
import com.seriesly.core.ui.component.RatingBottomSheet
import com.seriesly.core.ui.tokens.ContentSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    tvdbId: Int,
    onBack: () -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                MovieDetailEvent.NavigateBack -> onBack()
            }
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(uiState.movie?.title ?: "") },
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
                    IconButton(onClick = { viewModel.onIntent(MovieDetailIntent.ShowWatchlistSheet) }) {
                        Icon(
                            imageVector        = if (uiState.inWatchlistIds.isNotEmpty()) Icons.Filled.Bookmark
                                                 else Icons.Outlined.BookmarkAdd,
                            contentDescription = "Add to watchlist",
                            tint               = bookmarkTint
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->
        when {
            uiState.isLoading && uiState.movie == null ->
                HeroSkeleton(modifier = Modifier.fillMaxSize().padding(padding))

            uiState.error != null && uiState.movie == null ->
                ErrorState(
                    error    = uiState.error!!,
                    onRetry  = {},
                    modifier = Modifier.fillMaxSize().padding(padding)
                )

            uiState.movie != null -> {
                val movie = uiState.movie!!
                LazyColumn(contentPadding = padding) {
                    item {
                        Box(Modifier.fillMaxWidth().height(250.dp)) {
                            PosterImage(
                                url                = movie.backdropUrl,
                                contentDescription = movie.title,
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
                                url                = movie.posterUrl,
                                contentDescription = movie.title,
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
                            Text(movie.title, style = MaterialTheme.typography.headlineMedium)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                movie.year?.let { Text("$it", style = MaterialTheme.typography.bodySmall) }
                                movie.runtimeMinutes?.let { Text("${it}m", style = MaterialTheme.typography.bodySmall) }
                                movie.status?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                            }

                            Spacer(Modifier.height(16.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                WatchedChip(
                                    isWatched = uiState.isWatched,
                                    onClick   = { viewModel.onIntent(MovieDetailIntent.ToggleWatched) }
                                )
                                if (uiState.userRating != null) {
                                    AssistChip(
                                        onClick = { viewModel.onIntent(MovieDetailIntent.ShowRatingSheet) },
                                        label   = { Text("\u2605 ${"%.1f".format(uiState.userRating)}") }
                                    )
                                } else {
                                    AssistChip(
                                        onClick = { viewModel.onIntent(MovieDetailIntent.ShowRatingSheet) },
                                        label   = { Text("Rate") }
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            if (movie.genres.isNotEmpty()) {
                                Text(
                                    text  = movie.genres.joinToString(" · "),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                            Text(movie.overview, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
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
        targetValue   = if (isWatched) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
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
        verticalAlignment      = Alignment.CenterVertically,
        horizontalArrangement  = Arrangement.spacedBy(6.dp)
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
