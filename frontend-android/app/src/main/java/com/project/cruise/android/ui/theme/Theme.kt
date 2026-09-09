package com.project.cruise.android.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val OceanDarkColors = darkColorScheme(
    primary = OceanMint,
    onPrimary = Color(0xFF003736),
    primaryContainer = OceanTeal,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFAFC9E6),
    onSecondary = Color(0xFF12304E),
    background = Color(0xFF0C1824),
    onBackground = Color(0xFFE3EBF2),
    surface = Color(0xFF111F2D),
    onSurface = Color(0xFFE3EBF2),
    surfaceVariant = Color(0xFF1B2D3C),
    onSurfaceVariant = Color(0xFFC1CDD6),
    tertiary = Color(0xFFFFB4AA)
)

private val OceanLightColors = lightColorScheme(
    primary = OceanTeal,
    onPrimary = Color.White,
    primaryContainer = OceanMintSoft,
    onPrimaryContainer = OceanNavy,
    secondary = OceanNavy,
    onSecondary = Color.White,
    secondaryContainer = OceanLavender,
    onSecondaryContainer = OceanNavy,
    tertiary = OceanCoral,
    onTertiary = Color.White,
    tertiaryContainer = OceanCoralSoft,
    onTertiaryContainer = OceanInk,
    background = OceanSand,
    onBackground = OceanInk,
    surface = OceanPearl,
    onSurface = OceanInk,
    surfaceVariant = Color.White,
    onSurfaceVariant = OceanSlate,
    outline = Color(0xFF718087),
    outlineVariant = OceanLine,
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6)
)

val OceanShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun CruiseManagementTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) OceanDarkColors else OceanLightColors,
        typography = Typography,
        shapes = OceanShapes,
        content = content
    )
}
