package com.seriesly.feature.watchlist.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seriesly.core.domain.model.Watchlist
import com.seriesly.core.ui.component.EmptyState
import com.seriesly.core.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistListScreen(
    onNavigateToDetail: (Long) -> Unit,
    viewModel: WatchlistListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showCreateDialog by remember { mutableStateOf(false) }
    var watchlistToDelete by remember { mutableStateOf<Watchlist?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is WatchlistListEvent.ShowCreateDialog -> showCreateDialog = true
                is WatchlistListEvent.ShowSnackbar     -> scope.launch { snackbarHostState.showSnackbar(event.message) }
                is WatchlistListEvent.NavigateToDetail -> onNavigateToDetail(event.watchlistId)
            }
        }
    }

    if (showCreateDialog) {
        CreateWatchlistDialog(
            onConfirm = { name ->
                viewModel.onIntent(WatchlistListIntent.CreateConfirmed(name))
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    watchlistToDelete?.let { wl ->
        DeleteWatchlistDialog(
            watchlistName = wl.name,
            onConfirm = {
                viewModel.onIntent(WatchlistListIntent.DeleteClicked(wl))
                watchlistToDelete = null
            },
            onDismiss = { watchlistToDelete = null }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor   = OnSurface,
            snackbarHost   = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick          = { viewModel.onIntent(WatchlistListIntent.CreateClicked) },
                    containerColor   = PrimaryContainer,
                    contentColor     = OnPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create watchlist")
                }
            }
        ) { padding ->
            if (!uiState.isLoading && uiState.watchlists.isEmpty()) {
                EmptyState(
                    icon     = Icons.Outlined.Bookmarks,
                    title    = "No watchlists yet",
                    message  = "Tap + to create your first watchlist",
                    modifier = Modifier.fillMaxSize().padding(padding)
                )
            } else {
                LazyColumn(contentPadding = padding) {

                    // ── Editorial header ──────────────────────────────────
                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector        = Icons.Outlined.Bookmarks,
                                    contentDescription = null,
                                    tint               = Secondary,
                                    modifier           = Modifier.size(14.dp)
                                )
                                Text(
                                    text  = "YOUR COLLECTION",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Outline
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text  = "My Watchlists",
                                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = OnSurface
                            )
                            Text(
                                text  = "Your curated shelves of cinematic picks.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // ── Watchlist cards ───────────────────────────────────
                    items(uiState.watchlists, key = { it.watchlistId }) { watchlist ->
                        WatchlistCard(
                            watchlist = watchlist,
                            onClick   = { viewModel.onIntent(WatchlistListIntent.WatchlistSelected(watchlist.watchlistId)) },
                            onDelete  = if (!watchlist.isDefault) {{ watchlistToDelete = watchlist }} else null
                        )
                    }

                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun WatchlistCard(
    watchlist: Watchlist,
    onClick: () -> Unit,
    onDelete: (() -> Unit)?
) {
    val totalItems = watchlist.movieCount + watchlist.seriesCount

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier              = Modifier.weight(1f)
        ) {
            Icon(
                imageVector        = Icons.Outlined.Bookmarks,
                contentDescription = null,
                tint               = Primary,
                modifier           = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text     = watchlist.name,
                    style    = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color    = OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (totalItems > 0) {
                    Text(
                        text  = "$totalItems item${if (totalItems != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }
        if (onDelete != null) {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector        = Icons.Default.Delete,
                    contentDescription = "Delete ${watchlist.name}",
                    tint               = Outline
                )
            }
        }
    }
}

@Composable
private fun CreateWatchlistDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title            = { Text("New Watchlist") },
        text             = {
            OutlinedTextField(
                value          = name,
                onValueChange  = { if (it.length <= 50) name = it },
                label          = { Text("Name") },
                singleLine     = true,
                supportingText = { Text("${name.length}/50") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim()) },
                enabled = name.isNotBlank()
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun DeleteWatchlistDialog(
    watchlistName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title            = { Text("Delete Watchlist") },
        text             = { Text("Delete \"$watchlistName\"? This cannot be undone.") },
        confirmButton    = {
            TextButton(
                onClick = onConfirm,
                colors  = ButtonDefaults.textButtonColors(contentColor = Error)
            ) { Text("Delete") }
        },
        dismissButton    = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
