package com.gravity.ourmoments.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Our Moments custom colors
val SoftPurple = Color(0xFFA88CBC)
val WarmPink = Color(0xFFF4C2C2)
val Cream = Color(0xFFFFF8E1)
val LightBrown = Color(0xFFD7CCC8)
val DarkBrown = Color(0xFF5D4037)

// 手绘风格扩展颜色
val DustyPink = Color(0xFFE0BBE4)
val Mauve = Color(0xFFD291BC)
val Peach = Color(0xFFFEC8D8)
val Paper = Color(0xFFF9F5F0)
val KraftPaper = Color(0xFFD4C4A8)
val Watercolor = Color(0xFFF5E6D3)
val InkLight = Color(0xFF4A4A4A)
val InkDark = Color(0xFF2C2C2C)
val InkFaded = Color(0xFF8C8C8C)
val Blush = Color(0xFFFFDFD3)
val Mint = Color(0xFFB5EAD7)

// 胶带颜色
val TapeYellow = Color(0xFFFFE8D1)
val TapePink = Color(0xFFFFC8D8)
val TapeBlue = Color(0xFFADD8E6)
val TapeGreen = Color(0xFFB5EAD7)
val TapePurple = Color(0xFFE0BBE4)

// 图钉颜色
val PinRed = Color(0xFFDC143C)
val PinBlue = Color(0xFF1E90FF)
val PinGreen = Color(0xFF32CD32)
val PinYellow = Color(0xFFFFD700)
val PinPurple = Color(0xFF9932CC)

// 纸张纹理颜色
val PaperSurface = Color(0xFFF9F5F0)
val PaperDark = Color(0xFFF0EAD6)
val PaperShadow = Color(0x334A4A4A)

val PaperTheme = lightColorScheme(
    // Material 3 标准颜色
    primary = SoftPurple,
    onPrimary = Color.White,
    primaryContainer = DustyPink,
    onPrimaryContainer = InkLight,

    secondary = Mauve,
    onSecondary = Color.White,

    tertiary = Peach,
    onTertiary = InkLight,

    background = Paper,
    onBackground = InkLight,

    surface = Color.White,
    onSurface = InkLight,

    error = Color(0xFFBA1A1A),
    onError = Color.White,

    // 自定义手绘风格颜色
    surfaceVariant = PaperSurface,
    onSurfaceVariant = InkFaded
)