package com.buddy.web.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Terminal Warmth v1.0 — Design System
object BuddyColors {
    val Background = Color(0xFF080A10)
    val Surface = Color(0xFF0E1018)
    val Border = Color(0xFF1A1D2E)
    val SurfaceHover = Color(0xFF141722)

    val Amber = Color(0xFFF5A623)
    val AmberDim = Color(0x33F5A623)
    val Teal = Color(0xFF00D4B4)
    val TealDim = Color(0x3300D4B4)
    val Red = Color(0xFFFF4D6A)
    val Purple = Color(0xFF9B5CF6)

    val TextPrimary = Color(0xFFE8E6DF)
    val TextMid = Color(0xFF9CA3AF)
    val TextDim = Color(0xFF6B7280)
}

private val BuddyDarkScheme = darkColorScheme(
    primary = BuddyColors.Amber,
    onPrimary = BuddyColors.Background,
    secondary = BuddyColors.Teal,
    onSecondary = BuddyColors.Background,
    tertiary = BuddyColors.Purple,
    background = BuddyColors.Background,
    surface = BuddyColors.Surface,
    surfaceVariant = BuddyColors.Border,
    onBackground = BuddyColors.TextPrimary,
    onSurface = BuddyColors.TextPrimary,
    onSurfaceVariant = BuddyColors.TextMid,
    error = BuddyColors.Red,
    outline = BuddyColors.Border,
)

private val BuddyTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.5).sp,
        color = BuddyColors.TextPrimary,
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        color = BuddyColors.TextPrimary,
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        color = BuddyColors.TextPrimary,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        color = BuddyColors.TextPrimary,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        color = BuddyColors.TextPrimary,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = BuddyColors.TextPrimary,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = BuddyColors.TextMid,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = BuddyColors.TextMid,
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = BuddyColors.TextDim,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp,
        color = BuddyColors.Amber,
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = BuddyColors.TextMid,
    ),
)

@Composable
fun BuddyWebTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BuddyDarkScheme,
        typography = BuddyTypography,
        content = content,
    )
}
