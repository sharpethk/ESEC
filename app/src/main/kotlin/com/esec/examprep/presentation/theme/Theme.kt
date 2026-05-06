package com.esec.examprep.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary             = PrimaryBlue,
    onPrimary           = Color.White,
    primaryContainer    = PrimaryBlueSoft,
    onPrimaryContainer  = PrimaryBlueDark,
    secondary           = SecondaryGold,
    onSecondary         = Color.White,
    secondaryContainer  = SkippedAmberLight,
    onSecondaryContainer = SecondaryGoldDark,
    tertiary            = AccentTeal,
    background          = BackgroundLight,
    onBackground        = Neutral900,
    surface             = SurfaceLight,
    onSurface           = Neutral900,
    surfaceVariant      = SurfaceVariantLight,
    onSurfaceVariant    = Neutral600,
    outline             = Neutral300,
    outlineVariant      = Neutral200,
    error               = WrongRed,
    onError             = Color.White,
    errorContainer      = WrongRedLight,
)

private val DarkColorScheme = darkColorScheme(
    primary             = Color(0xFF7DA8FF),
    onPrimary           = Color(0xFF0A1E47),
    primaryContainer    = Color(0xFF223A6E),
    onPrimaryContainer  = Color(0xFFD7E3FF),
    secondary           = Color(0xFFFFC067),
    onSecondary         = Color(0xFF3F2700),
    secondaryContainer  = Color(0xFF5A3A00),
    onSecondaryContainer = Color(0xFFFFE0B5),
    tertiary            = Color(0xFF5DD9D8),
    background          = BackgroundDark,
    onBackground        = Neutral100,
    surface             = SurfaceDark,
    onSurface           = Neutral100,
    surfaceVariant      = SurfaceVariantDark,
    onSurfaceVariant    = Neutral300,
    outline             = OutlineDark,
    outlineVariant      = Color(0xFF222934),
    error               = Color(0xFFFF6B6B),
    onError             = Color(0xFF3B0000),
    errorContainer      = Color(0xFF5A1010),
)

@Composable
fun ESECTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // disabled by default — keep branded look
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
