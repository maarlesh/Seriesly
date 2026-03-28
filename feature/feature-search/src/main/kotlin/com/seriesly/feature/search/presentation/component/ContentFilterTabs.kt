package com.seriesly.feature.search.presentation.component

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.seriesly.core.domain.model.ContentFilter

@Composable
fun ContentFilterTabs(selected: ContentFilter, onSelected: (ContentFilter) -> Unit) {
    val filters = ContentFilter.entries
    TabRow(selectedTabIndex = filters.indexOf(selected)) {
        filters.forEach { filter ->
            Tab(
                selected = filter == selected,
                onClick  = { onSelected(filter) },
                text = { Text(filter.name.lowercase().replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}
