package com.seriesly.feature.detail.presentation.series.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seriesly.core.domain.model.Episode
import com.seriesly.core.domain.model.Season
import com.seriesly.core.ui.theme.WatchedGold
import com.seriesly.core.ui.theme.WatchedGoldSubtle

@Composable
fun SeasonSection(
    season: Season,
    expandedSeasonIds: Set<Int>,
    episodeWatched: Map<Int, Boolean>,
    onToggleExpand: (Int) -> Unit,
    onEpisodeToggle: (Episode, Boolean) -> Unit,
    onMarkSeasonWatched: (Season) -> Unit
) {
    val watchedCount    = season.episodes.count { episodeWatched[it.episodeId] == true }
    val enrichedSeason  = season.copy(watchedCount = watchedCount)
    val isExpanded      = season.seasonId in expandedSeasonIds

    val animatedProgress by animateFloatAsState(
        targetValue   = enrichedSeason.progressPercent,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
        label         = "seasonProgress"
    )
    val cardOverlay by animateColorAsState(
        targetValue   = if (enrichedSeason.isComplete) WatchedGoldSubtle else androidx.compose.ui.graphics.Color.Transparent,
        animationSpec = tween(500),
        label         = "seasonBg"
    )
    val progressColor by animateColorAsState(
        targetValue   = if (enrichedSeason.isComplete) WatchedGold else MaterialTheme.colorScheme.primary,
        animationSpec = tween(400),
        label         = "progressColor"
    )

    ElevatedCard(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape     = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Box(Modifier.background(cardOverlay)) {
            Column {
                ListItem(
                    headlineContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text  = "Season ${season.seasonNumber}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            AnimatedVisibility(visible = enrichedSeason.isComplete) {
                                Icon(
                                    imageVector        = Icons.Filled.CheckCircle,
                                    contentDescription = "Season complete",
                                    tint               = WatchedGold,
                                    modifier           = Modifier.padding(start = 6.dp).size(16.dp)
                                )
                            }
                        }
                    },
                    supportingContent = {
                        Column {
                            Text(
                                text  = "$watchedCount / ${season.episodeCount} watched",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.fillMaxWidth(),
                                color    = progressColor
                            )
                        }
                    },
                    trailingContent = {
                        Row {
                            AnimatedVisibility(
                                visible = !enrichedSeason.isComplete,
                                exit    = fadeOut() + scaleOut()
                            ) {
                                IconButton(onClick = { onMarkSeasonWatched(season) }) {
                                    Icon(
                                        imageVector        = Icons.Outlined.DoneAll,
                                        contentDescription = "Mark season watched"
                                    )
                                }
                            }
                            IconButton(onClick = { onToggleExpand(season.seasonId) }) {
                                Icon(
                                    imageVector        = if (isExpanded) Icons.Filled.ExpandLess
                                                         else Icons.Filled.ExpandMore,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
                AnimatedVisibility(
                    visible = isExpanded,
                    enter   = expandVertically(spring(dampingRatio = 0.8f, stiffness = 300f)) + fadeIn(),
                    exit    = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        season.episodes.forEach { episode ->
                            EpisodeRow(
                                episode   = episode,
                                isWatched = episodeWatched[episode.episodeId] == true,
                                onToggle  = { onEpisodeToggle(episode, it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
