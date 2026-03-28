package com.seriesly.feature.progress.presentation.ratings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.domain.model.RatedItem
import com.seriesly.core.ui.component.ContentTypeBadge
import com.seriesly.core.ui.component.EmptyState
import com.seriesly.core.ui.component.PosterImage
import com.seriesly.core.ui.tokens.ContentSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRatingsScreen(
    onNavigateToDetail: (Int, ContentType) -> Unit,
    viewModel: MyRatingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is MyRatingsEvent.NavigateToDetail ->
                    onNavigateToDetail(event.tvdbId, event.contentType)
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Ratings") }) }
    ) { padding ->
        when {
            uiState.ratings.isEmpty() ->
                EmptyState(
                    icon     = Icons.Outlined.StarOutline,
                    title    = "No ratings yet",
                    message  = "Rate movies and series from their detail screens",
                    modifier = Modifier.fillMaxSize().padding(padding)
                )
            else ->
                LazyColumn(contentPadding = padding) {
                    items(uiState.ratings, key = { "${it.tvdbId}-${it.contentType}" }) { rated ->
                        RatedItemCard(
                            item    = rated,
                            onClick = { viewModel.onIntent(MyRatingsIntent.ItemClicked(rated.tvdbId, rated.contentType)) }
                        )
                    }
                }
        }
    }
}

@Composable
private fun RatedItemCard(item: RatedItem, onClick: () -> Unit) {
    Card(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PosterImage(
                url                = item.posterUrl,
                contentDescription = item.title,
                width              = ContentSize.posterWidth,
                height             = ContentSize.posterHeight
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ContentTypeBadge(item.contentType)
                Text(item.title, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        val filled = (index + 1) <= item.rating
                        Icon(
                            imageVector        = if (filled) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint               = MaterialTheme.colorScheme.tertiary,
                            modifier           = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("${"%.1f".format(item.rating)}", style = MaterialTheme.typography.labelSmall)
                }
                item.comment?.let {
                    Text(
                        text     = it,
                        style    = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
