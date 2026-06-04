package com.yousefalaa.electronicmuezzin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val GoldPrimary  = Color(0xFFD4AF37)
val GoldLight    = Color(0xFFFFDF6B)
val GoldDark     = Color(0xFF9E7A1A)
val GreenIslamic = Color(0xFF2E7D32)
val GreenLight   = Color(0xFF4CAF50)
val DarkBackground = Color(0xFF0D1B2A)
val DarkSurface  = Color(0xFF1A2E40)
val DarkCard     = Color(0xFF1E3A4A)

// تيم فاتح فقط - يضمن ظهور النصوص العربية
val AppColorScheme = lightColorScheme(
    primary          = Color(0xFFD4573A),
    onPrimary        = Color.White,
    secondary        = GreenIslamic,
    onSecondary      = Color.White,
    background       = Color(0xFFF2F2F7),
    onBackground     = Color(0xFF1A1A1A),
    surface          = Color.White,
    onSurface        = Color(0xFF1A1A1A),
    surfaceVariant   = Color(0xFFF2F2F7),
    onSurfaceVariant = Color(0xFF444444),
    outline          = Color(0xFFE0E0E0)
)

@Composable
fun ElectronicMuezzinTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography  = MuezzinTypography,
        content     = content
    )
}
