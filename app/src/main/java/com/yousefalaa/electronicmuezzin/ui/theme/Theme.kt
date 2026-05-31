package com.yousefalaa.electronicmuezzin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ألوان إسلامية أصيلة
val GoldPrimary = Color(0xFFD4AF37)
val GoldLight = Color(0xFFFFDF6B)
val GoldDark = Color(0xFF9E7A1A)
val GreenIslamic = Color(0xFF2E7D32)
val GreenLight = Color(0xFF4CAF50)
val DarkBackground = Color(0xFF0D1B2A)
val DarkSurface = Color(0xFF1A2E40)
val DarkCard = Color(0xFF1E3A4A)
val LightBeige = Color(0xFFF5F0E8)
val LightBeigeCard = Color(0xFFEDE8DC)
val NightBlue = Color(0xFF0A1628)
val MorningGold = Color(0xFFFF8F00)
val EveningPurple = Color(0xFF4A148C)

val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = GoldDark,
    onPrimaryContainer = Color.White,
    secondary = GreenIslamic,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1B4332),
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkCard,
    onSurfaceVariant = Color(0xFFCCCCCC),
    outline = GoldDark
)

val LightColorScheme = lightColorScheme(
    primary = GreenIslamic,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA5D6A7),
    onPrimaryContainer = Color(0xFF1B4332),
    secondary = GoldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF8E1),
    background = LightBeige,
    onBackground = Color(0xFF1A1A1A),
    surface = LightBeigeCard,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE8E3D5),
    onSurfaceVariant = Color(0xFF444444),
    outline = Color(0xFFB0A882)
)

@Composable
fun ElectronicMuezzinTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MuezzinTypography,
        content = content
    )
}
