package com.seriesly.feature.progress.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.domain.model.WatchHistoryEntry
import com.seriesly.core.ui.component.ContentTypeBadge
import com.seriesly.core.ui.component.EmptyState
import com.seriesly.core.ui.component.PosterImage
import com.seriesly.core.ui.theme.*
import com.seriesly.core.ui.tokens.ContentSize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (Int, ContentType) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HistoryEvent.NavigateToDetail ->
                    onNavigateToDetail(event.tvdbId, event.contentType)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "Watch History",
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState.entries.isNotEmpty()) {
                            Text(
                                text  = "${uiState.entries.size} titles",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        containerColor = Background
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            uiState.entries.isEmpty() -> {
                EmptyState(
                    icon     = Icons.Outlined.HistoryEdu,
                    title    = "No watch history yet",
                    message  = "Start watching movies and series to build your diary",
                    modifier = Modifier.fillMaxSize().padding(padding)
                )
            }
            else -> {
                val grouped = uiState.entries.groupBy { entry ->
                    SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(entry.watchedAt))
                }
                LazyColumn(
                    contentPadding = PaddingValues(
                        start  = 16.dp,
                        end    = 16.dp,
                        top    = padding.calculateTopPadding() + 8.dp,
                        bottom = padding.calculateBottomPadding() + 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    grouped.forEach { (monthYear, entries) ->
                        item(key = monthYear) {
                            Text(
                                text     = monthYear,
                                style    = MaterialTheme.typography.labelMedium,
                                color    = Outline,
                                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                            )
                        }
                        items(entries, key = { "${it.tvdbId}_${it.contentType}_${it.watchedAt}" }) { entry ->
                            HistoryItemCard(
                                entry   = entry,
                                onClick = { viewModel.onIntent(HistoryIntent.ItemClicked(entry.tvdbId, entry.contentType)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemCard(entry: WatchHistoryEntry, onClick: () -> Unit) {
    Card(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
    ) {
        Row(
            modifier              = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            PosterImage(
                url                = entry.posterUrl,
                contentDescription = entry.title,
                width              = ContentSize.posterWidth,
                height             = ContentSize.posterHeight
            )
            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ContentTypeBadge(entry.contentType)
                Text(
                    text     = entry.title,
                    style    = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    color    = OnSurface
                )
                Text(
                    text  = formatWatchedDate(entry.watchedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
            }
            entry.rating?.let { rating ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Tertiary.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text  = "★ ${"%.1f".format(rating)}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Tertiary
                    )
                }
            }
        }
    }
}

private fun formatWatchedDate(epochMs: Long): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(epochMs))
