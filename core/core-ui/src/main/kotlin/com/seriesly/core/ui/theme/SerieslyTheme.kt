package com.seriesly.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Cinematic Obsidian — dark-only design. Light scheme intentionally absent for v1.1.
private val SerieslyDarkColorScheme = darkColorScheme(
    primary              = Primary,
    onPrimary            = OnPrimary,
    primaryContainer     = PrimaryContainer,
    onPrimaryContainer   = OnPrimaryContainer,
    inversePrimary       = InversePrimary,

    secondary            = Secondary,
    onSecondary          = OnSecondary,
    secondaryContainer   = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,

    tertiary             = Tertiary,
    onTertiary           = OnTertiary,
    tertiaryContainer    = TertiaryContainer,
    onTertiaryContainer  = OnTertiaryContainer,

    background           = Background,
    onBackground         = OnBackground,

    surface              = Surface,
    onSurface            = OnSurface,
    surfaceVariant       = SurfaceVariant,
    onSurfaceVariant     = OnSurfaceVariant,
    surfaceTint          = SurfaceTint,
    inverseSurface       = InverseSurface,
    inverseOnSurface     = InverseOnSurface,

    surfaceContainer          = SurfaceContainer,
    surfaceContainerLow       = SurfaceContainerLow,
    surfaceContainerHigh      = SurfaceContainerHigh,
    surfaceContainerHighest   = SurfaceContainerHighest,
    surfaceContainerLowest    = SurfaceContainerLowest,
    surfaceBright             = SurfaceBright,
    surfaceDim                = SurfaceDim,

    outline              = Outline,
    outlineVariant       = OutlineVariant,

    error                = Error,
    onError              = OnError,
    errorContainer       = ErrorContainer,
    onErrorContainer     = OnErrorContainer,
)

@Composable
fun SerieslyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SerieslyDarkColorScheme,
        typography  = SerieslyTypography,
        shapes      = SerieslyShapes,
        content     = content
    )
}
