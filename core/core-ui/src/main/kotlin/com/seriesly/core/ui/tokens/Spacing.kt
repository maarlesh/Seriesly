package com.seriesly.core.ui.tokens

import androidx.compose.ui.unit.dp

/**
 * Seriesly spacing scale — 4dp base grid.
 * Use these tokens instead of raw dp values in all composables.
 *
 * Usage:  Modifier.padding(Spacing.md)
 */
object Spacing {
    /** 2dp — hairline dividers only */
    val hairline = 2.dp
    /** 4dp */
    val xs = 4.dp
    /** 8dp */
    val sm = 8.dp
    /** 12dp */
    val smMd = 12.dp
    /** 16dp — default content padding */
    val md = 16.dp
    /** 20dp */
    val mdLg = 20.dp
    /** 24dp */
    val lg = 24.dp
    /** 32dp */
    val xl = 32.dp
    /** 48dp */
    val xxl = 48.dp
    /** 64dp */
    val xxxl = 64.dp
}

/**
 * Minimum touch target size per accessibility guidelines.
 */
object TouchTarget {
    val min = 48.dp
}

/**
 * Content sizing constants shared across screens.
 */
object ContentSize {
    /** Poster image — list row */
    val posterWidth  = 60.dp
    val posterHeight = 90.dp
    /** Poster image — detail hero */
    val posterDetailWidth  = 120.dp
    val posterDetailHeight = 180.dp
    /** Bottom nav height */
    val bottomNavHeight = 80.dp
}
