package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = RapidoYellow,
    onPrimary = RapidoBlack,
    primaryContainer = RapidoCharcoal,
    onPrimaryContainer = RapidoYellow,
    secondary = RapidoYellow,
    onSecondary = RapidoBlack,
    secondaryContainer = RapidoCharcoal,
    onSecondaryContainer = Color.White,
    tertiary = TransitTealTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = RapidoBlack,
    onPrimary = Color.White,
    primaryContainer = RapidoYellow,
    onPrimaryContainer = RapidoBlack,
    secondary = RapidoYellow,
    onSecondary = RapidoBlack,
    secondaryContainer = RapidoYellowLight,
    onSecondaryContainer = RapidoBlack,
    tertiary = TransitTealTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE8F5E9),
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onBackground = RapidoBlack,
    onSurface = RapidoBlack,
    outline = CardBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set false to maintain signature Rapido-inspired branding
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
