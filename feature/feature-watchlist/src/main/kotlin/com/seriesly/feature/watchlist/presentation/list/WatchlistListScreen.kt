package com.seriesly.feature.watchlist.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seriesly.core.domain.model.Watchlist
import com.seriesly.core.ui.component.EmptyState
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
                is WatchlistListEvent.ShowCreateDialog   -> showCreateDialog = true
                is WatchlistListEvent.ShowSnackbar       -> scope.launch { snackbarHostState.showSnackbar(event.message) }
                is WatchlistListEvent.NavigateToDetail   -> onNavigateToDetail(event.watchlistId)
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

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Watchlists") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onIntent(WatchlistListIntent.CreateClicked) }) {
                Icon(Icons.Default.Add, contentDescription = "Create watchlist")
            }
        }
    ) { padding ->
        if (!uiState.isLoading && uiState.watchlists.isEmpty()) {
            EmptyState(
                icon    = Icons.Outlined.Bookmarks,
                title   = "No watchlists yet",
                message = "Tap + to create your first watchlist",
                modifier = Modifier.fillMaxSize().padding(padding)
            )
        } else {
            LazyColumn(contentPadding = padding) {
                items(uiState.watchlists, key = { it.watchlistId }) { watchlist ->
                    WatchlistRow(
                        watchlist = watchlist,
                        onClick   = { viewModel.onIntent(WatchlistListIntent.WatchlistSelected(watchlist.watchlistId)) },
                        onDelete  = if (!watchlist.isDefault) {{ watchlistToDelete = watchlist }} else null
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun WatchlistRow(
    watchlist: Watchlist,
    onClick: () -> Unit,
    onDelete: (() -> Unit)?
) {
    ListItem(
        headlineContent = {
            Text(watchlist.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        leadingContent = {
            Icon(Icons.Outlined.Bookmarks, contentDescription = null)
        },
        trailingContent = if (onDelete != null) {{
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete ${watchlist.name}")
            }
        }} else null,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun CreateWatchlistDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Watchlist") },
        text = {
            OutlinedTextField(
                value         = name,
                onValueChange = { if (it.length <= 50) name = it },
                label         = { Text("Name") },
                singleLine    = true,
                supportingText = { Text("${name.length}/50") }
            )
        },
        confirmButton = {
            TextButton(
                onClick  = { if (name.isNotBlank()) onConfirm(name.trim()) },
                enabled  = name.isNotBlank()
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
        title = { Text("Delete Watchlist") },
        text  = { Text("Delete \"$watchlistName\"? This cannot be undone.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors  = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
