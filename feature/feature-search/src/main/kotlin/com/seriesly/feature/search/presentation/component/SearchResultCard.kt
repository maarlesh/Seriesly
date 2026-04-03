package com.seriesly.feature.search.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.core.ui.component.ContentTypeBadge
import com.seriesly.core.ui.component.PosterImage
import com.seriesly.core.ui.theme.OnSurface
import com.seriesly.core.ui.theme.OnSurfaceVariant
import com.seriesly.core.ui.theme.SurfaceContainerLow
import com.seriesly.core.ui.theme.Tertiary

@Composable
fun SearchResultCard(item: ContentItem, onClick: () -> Unit) {
    val accentColor = if (item.contentType == ContentType.SERIES)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.tertiary

    Card(
        onClick   = onClick,
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape     = MaterialTheme.shapes.large,
        colors    = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
    ) {
        Row(
            modifier = Modifier
                .drawBehind {
                    drawRect(
                        color  = accentColor,
                        size   = size.copy(width = 3.dp.toPx())
                    )
                }
                .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PosterImage(
                url                = item.posterUrl,
                contentDescription = item.title
            )
            Column(Modifier.weight(1f)) {
                ContentTypeBadge(item.contentType)
                Spacer(Modifier.height(4.dp))
                Text(item.title, style = MaterialTheme.typography.titleMedium, color = OnSurface, maxLines = 2)
                item.year?.let {
                    Text(
                        "$it",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
                item.tvdbRating?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint               = Tertiary,
                            modifier           = Modifier.size(14.dp)
                        )
                        Text(" %.1f".format(it), style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                }
            }
        }
    }
}
