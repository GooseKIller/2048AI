package com.example.a2048game.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val Red400 = Color(0xFFCF6679)

// Cells colors
val BackgroundCell = Color(186, 172, 159, 255)
val Cell0 = Color(203, 191, 179, 255)
val Cell2 = Color(238, 228, 218)
val Cell4 = Color(237, 224, 200)
val Cell8 = Color(242, 177, 121)
val Cell16 = Color( 235, 149, 99)
val Cell32 = Color(246, 124, 95)
val Cell64 = Color(246, 94, 59)
val Cell128 = Color(237, 207, 114)
val Cell256 = Color(237,204,97)
val Cell512 = Color(237,200,80)
val Cell1024 = Color(237,197,63)
val Cell2048 = Color(237,197,1)
val CellMore = Color(94,218,146)

internal val wearColorPalette: Colors = Colors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200,
    secondaryVariant = Teal200,
    error = Red400,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onError = Color.Black
)