package com.seriesly.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seriesly.core.common.model.ContentType

@Composable
fun ContentTypeBadge(
    type: ContentType,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (type) {
        ContentType.MOVIE  -> MaterialTheme.colorScheme.tertiary to "MOVIE"
        ContentType.SERIES -> MaterialTheme.colorScheme.primary  to "SERIES"
    }
    BadgePill(label = label, color = color, modifier = modifier)
}

@Composable
fun BadgePill(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text  = label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
