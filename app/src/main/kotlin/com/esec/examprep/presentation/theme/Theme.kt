package com.esec.examprep.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary                 = Primary,
    onPrimary               = OnPrimary,
    primaryContainer        = PrimaryContainer,
    onPrimaryContainer      = OnPrimaryContainer,
    inversePrimary          = InversePrimary,
    secondary               = Secondary,
    onSecondary             = OnSecondary,
    secondaryContainer      = SecondaryContainer,
    onSecondaryContainer    = OnSecondaryContainer,
    tertiary                = Tertiary,
    onTertiary              = OnTertiary,
    tertiaryContainer       = TertiaryContainer,
    onTertiaryContainer     = OnTertiaryContainer,
    error                   = ErrorRed,
    onError                 = OnErrorRed,
    errorContainer          = ErrorContainer,
    onErrorContainer        = OnErrorContainer,
    background              = Background,
    onBackground            = OnBackground,
    surface                 = Surface,
    onSurface               = OnSurface,
    onSurfaceVariant        = OnSurfaceVariant,
    surfaceContainerLowest  = SurfaceContainerLowest,
    surfaceContainerLow     = SurfaceContainerLow,
    surfaceContainer        = SurfaceContainer,
    surfaceContainerHigh    = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    outline                 = OutlineColor,
    outlineVariant          = OutlineVariantColor,
)

private val DarkColorScheme = darkColorScheme(
    primary                 = PrimaryDark,
    onPrimary               = OnPrimaryDark,
    primaryContainer        = PrimaryContainerDark,
    onPrimaryContainer      = OnPrimaryContainerDark,
    inversePrimary          = Primary,
    secondary               = SecondaryDark,
    onSecondary             = OnSecondaryDark,
    secondaryContainer      = SecondaryContainerDark,
    onSecondaryContainer    = OnSecondaryContainerDark,
    tertiary                = TertiaryDark,
    onTertiary              = OnTertiaryDark,
    tertiaryContainer       = TertiaryContainerDark,
    onTertiaryContainer     = OnTertiaryContainerDark,
    background              = BackgroundDarkScheme,
    onBackground            = OnBackgroundDarkScheme,
    surface                 = SurfaceDarkScheme,
    onSurface               = OnSurfaceDarkScheme,
    onSurfaceVariant        = OnSurfaceVariantDark,
    surfaceContainerLowest  = SurfaceContainerLowestDark,
    surfaceContainerLow     = SurfaceContainerLowDark,
    surfaceContainer        = SurfaceContainerDarkScheme,
    surfaceContainerHigh    = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
    outline                 = OutlineDarkScheme,
    outlineVariant          = OutlineVariantDarkScheme,
)

@Composable
fun ESECTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // disabled by default — keep branded Eritrean look
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = ESECTypography,
        shapes      = ESECShapes,
        content     = content,
    )
}
