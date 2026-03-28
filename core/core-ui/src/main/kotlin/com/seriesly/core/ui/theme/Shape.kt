package com.seriesly.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val SerieslyShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),   // badges, chips
    small      = RoundedCornerShape(10.dp),  // poster corners, small cards
    medium     = RoundedCornerShape(16.dp),  // cards, dialogs
    large      = RoundedCornerShape(20.dp),  // bottom sheets, large cards
    extraLarge = RoundedCornerShape(28.dp)   // FAB, search bar
)
