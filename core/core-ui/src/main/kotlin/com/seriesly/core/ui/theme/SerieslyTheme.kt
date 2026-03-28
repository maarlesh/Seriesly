package com.seriesly.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SerieslyDarkColorScheme = darkColorScheme(
    primary              = BrandPurple,
    onPrimary            = DarkOnError,           // white
    primaryContainer     = BrandPurpleDim,
    onPrimaryContainer   = DarkOnBackground,
    secondary            = BrandTeal,
    onSecondary          = DarkOnError,
    secondaryContainer   = DarkSurfaceVariant,
    onSecondaryContainer = DarkOnSurface,
    tertiary             = TertiaryAmber,
    onTertiary           = DarkBackground,
    tertiaryContainer    = TertiaryAmberDark,
    onTertiaryContainer  = DarkOnBackground,
    background           = DarkBackground,
    onBackground         = DarkOnBackground,
    surface              = DarkSurface,
    onSurface            = DarkOnSurface,
    surfaceVariant       = DarkSurfaceVariant,
    onSurfaceVariant     = DarkOnSurfaceVariant,
    surfaceContainer     = DarkSurfaceContainer,
    outline              = DarkOutline,
    error                = DarkError,
    onError              = DarkOnError,
    errorContainer       = DarkErrorContainer,
    onErrorContainer     = DarkOnBackground
)

private val SerieslyLightColorScheme = lightColorScheme(
    primary              = BrandPurpleLight,
    onPrimary            = LightOnError,           // white
    primaryContainer     = LightSurfaceVariant,
    onPrimaryContainer   = LightOnBackground,
    secondary            = BrandTeal,
    onSecondary          = LightOnError,
    secondaryContainer   = LightSurfaceContainer,
    onSecondaryContainer = LightOnSurface,
    tertiary             = TertiaryAmberDark,
    onTertiary           = LightOnError,
    tertiaryContainer    = LightSurfaceVariant,
    onTertiaryContainer  = LightOnBackground,
    background           = LightBackground,
    onBackground         = LightOnBackground,
    surface              = LightSurface,
    onSurface            = LightOnSurface,
    surfaceVariant       = LightSurfaceVariant,
    onSurfaceVariant     = LightOnSurfaceVariant,
    surfaceContainer     = LightSurfaceContainer,
    outline              = LightOutline,
    error                = LightError,
    onError              = LightOnError,
    errorContainer       = LightErrorContainer,
    onErrorContainer     = LightOnBackground
)

@Composable
fun SerieslyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) SerieslyDarkColorScheme else SerieslyLightColorScheme,
        typography  = SerieslyTypography,
        shapes      = SerieslyShapes,
        content     = content
    )
}
