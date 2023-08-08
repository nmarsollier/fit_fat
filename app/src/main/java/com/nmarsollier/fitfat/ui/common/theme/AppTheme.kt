package com.nmarsollier.fitfat.ui.common.theme

import androidx.compose.material3.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.*

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5B68FF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF3746E6),
    secondary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF5B68FF),
    secondaryContainer = Color(0xFFAAAAAA),
    onSecondaryContainer = Color(0xFF3746E6),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF202020),
    error = Color(0xFFEEEEEE),
    onError = Color(0xFFB00020),
)

object GlobalTextStyles {
    val bodyOnSurface = TextStyle(
        color = Color(0xFF202020),
        fontSize = 14.sp,
    )
    val bodyPrimary = TextStyle(
        color = Color(0xFF5B68FF),
        fontSize = 14.sp,
    )

}