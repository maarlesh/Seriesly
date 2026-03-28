package com.seriesly.feature.watchlist.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seriesly.core.domain.model.Watchlist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToWatchlistSheet(
    watchlists: List<Watchlist>,
    alreadyInIds: Set<Long>,
    onWatchlistToggled: (Long, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text     = "Add to Watchlist",
                style    = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            watchlists.forEach { wl ->
                val isIn = wl.watchlistId in alreadyInIds
                ListItem(
                    headlineContent = { Text(wl.name) },
                    leadingContent = {
                        Icon(
                            imageVector = if (isIn) Icons.Filled.Bookmark else Icons.Outlined.Bookmarks,
                            contentDescription = null,
                            tint = if (isIn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    },
                    modifier = Modifier.clickable { onWatchlistToggled(wl.watchlistId, !isIn) }
                )
            }
        }
    }
}
