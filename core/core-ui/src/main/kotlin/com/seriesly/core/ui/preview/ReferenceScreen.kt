package com.seriesly.core.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.common.ui.UiError
import com.seriesly.core.ui.component.BadgePill
import com.seriesly.core.ui.component.ContentTypeBadge
import com.seriesly.core.ui.component.EmptyState
import com.seriesly.core.ui.component.ErrorState
import com.seriesly.core.ui.component.PosterImage
import com.seriesly.core.ui.component.SearchResultSkeleton
import com.seriesly.core.ui.component.StarRatingDisplay
import com.seriesly.core.ui.component.StarRatingInput
import com.seriesly.core.ui.theme.SerieslyTheme
import com.seriesly.core.ui.tokens.Spacing

/**
 * ─────────────────────────────────────────────────────────────────────────────
 * SERIESLY REFERENCE SCREEN
 * ─────────────────────────────────────────────────────────────────────────────
 * This file is a DEVELOPER VISUAL CONTRACT — not a real screen.
 * It shows every design token and shared component in one scrollable surface.
 *
 * Rules:
 *  • Every new shared component MUST be added here with a preview section.
 *  • Every new color token MUST appear in the color swatch section.
 *  • This screen must render correctly in both dark and light @Preview.
 *  • No feature logic, no ViewModels, no real data. Hardcoded previews only.
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Composable
private fun ReferenceScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {

            // ── Section: Color Palette ────────────────────────────────────
            SectionHeader("Color Palette")
            ColorSwatch(MaterialTheme.colorScheme.primary,         "primary")
            ColorSwatch(MaterialTheme.colorScheme.primaryContainer,"primaryContainer")
            ColorSwatch(MaterialTheme.colorScheme.secondary,       "secondary")
            ColorSwatch(MaterialTheme.colorScheme.tertiary,        "tertiary")
            ColorSwatch(MaterialTheme.colorScheme.background,      "background")
            ColorSwatch(MaterialTheme.colorScheme.surface,         "surface")
            ColorSwatch(MaterialTheme.colorScheme.surfaceVariant,  "surfaceVariant")
            ColorSwatch(MaterialTheme.colorScheme.surfaceContainer,"surfaceContainer")
            ColorSwatch(MaterialTheme.colorScheme.error,           "error")
            ColorSwatch(MaterialTheme.colorScheme.outline,         "outline")

            HorizontalDivider()

            // ── Section: Typography ───────────────────────────────────────
            SectionHeader("Typography Scale")
            Text("displaySmall  — Series Title",   style = MaterialTheme.typography.displaySmall)
            Text("headlineMedium — Section Header", style = MaterialTheme.typography.headlineMedium)
            Text("titleLarge — Card Title",          style = MaterialTheme.typography.titleLarge)
            Text("titleMedium — Row Title",          style = MaterialTheme.typography.titleMedium)
            Text("titleSmall — Subtitle",            style = MaterialTheme.typography.titleSmall)
            Text("bodyLarge — Primary body copy",    style = MaterialTheme.typography.bodyLarge)
            Text("bodyMedium — Supporting text",     style = MaterialTheme.typography.bodyMedium)
            Text("bodySmall — Captions / metadata",  style = MaterialTheme.typography.bodySmall)
            Text("labelLarge — Button text",         style = MaterialTheme.typography.labelLarge)
            Text("labelSmall — Badge labels",        style = MaterialTheme.typography.labelSmall)

            HorizontalDivider()

            // ── Section: Spacing Tokens ───────────────────────────────────
            SectionHeader("Spacing Tokens")
            SpacingRow("xs  — 4dp",    Spacing.xs)
            SpacingRow("sm  — 8dp",    Spacing.sm)
            SpacingRow("smMd — 12dp",  Spacing.smMd)
            SpacingRow("md  — 16dp",   Spacing.md)
            SpacingRow("lg  — 24dp",   Spacing.lg)
            SpacingRow("xl  — 32dp",   Spacing.xl)
            SpacingRow("xxl — 48dp",   Spacing.xxl)

            HorizontalDivider()

            // ── Section: Badges ───────────────────────────────────────────
            SectionHeader("Badges")
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                ContentTypeBadge(ContentType.MOVIE)
                ContentTypeBadge(ContentType.SERIES)
                BadgePill(label = "WATCHED", color = MaterialTheme.colorScheme.secondary)
                BadgePill(label = "NEW",     color = MaterialTheme.colorScheme.error)
            }

            HorizontalDivider()

            // ── Section: Poster Image ─────────────────────────────────────
            SectionHeader("Poster Image (null URL → placeholder)")
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                PosterImage(url = null, contentDescription = "Placeholder poster")
                PosterImage(url = null, contentDescription = "Placeholder poster")
                PosterImage(url = null, contentDescription = "Placeholder poster")
            }

            HorizontalDivider()

            // ── Section: Star Rating ──────────────────────────────────────
            SectionHeader("Star Rating Input (interactive)")
            StarRatingInput(currentRating = 3.5f, onRatingChanged = {})

            SectionHeader("Star Rating Display (read-only)")
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                StarRatingDisplay(rating = 1.0f)
                StarRatingDisplay(rating = 2.5f)
                StarRatingDisplay(rating = 5.0f)
            }

            HorizontalDivider()

            // ── Section: Skeleton Loader ──────────────────────────────────
            SectionHeader("Skeleton Loader")
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                repeat(3) { SearchResultSkeleton() }
            }

            HorizontalDivider()

            // ── Section: Error State ──────────────────────────────────────
            SectionHeader("Error State")
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                ErrorState(
                    error   = UiError("No Connection", "Check your internet and try again"),
                    onRetry = {}
                )
            }

            HorizontalDivider()

            // ── Section: Empty State ──────────────────────────────────────
            SectionHeader("Empty State")
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                EmptyState(
                    icon    = Icons.Outlined.Bookmarks,
                    title   = "Your watchlist is empty",
                    message = "Search for a movie or series and add it here."
                )
            }

            Spacer(Modifier.height(Spacing.xxl))
        }
    }
}

// ── Helper composables ────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Text(
        text  = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = Spacing.xs)
    )
}

@Composable
private fun ColorSwatch(color: Color, name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp, 24.dp)
                .background(color, MaterialTheme.shapes.extraSmall)
        )
        Text(
            text  = name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SpacingRow(label: String, size: androidx.compose.ui.unit.Dp) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Box(
            modifier = Modifier
                .size(size, 16.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Reference Screen — Dark", showBackground = true, backgroundColor = 0xFF0F0F13, widthDp = 400, heightDp = 900)
@Composable
private fun ReferenceScreenDarkPreview() {
    SerieslyTheme(darkTheme = true) { ReferenceScreen() }
}

@Preview(name = "Reference Screen — Light", showBackground = true, backgroundColor = 0xFFF5F5FA, widthDp = 400, heightDp = 900)
@Composable
private fun ReferenceScreenLightPreview() {
    SerieslyTheme(darkTheme = false) { ReferenceScreen() }
}
