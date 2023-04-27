package com.hocel.chirrup.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

val blueBG = Color(0xFFF4F7FD)
val blueText = Color(0xFF1E3054)
val card = Color(0xFFFFFFFF)

val blue = Color(0xFF006AF6)
val blueNight = Color(0xFF147EFF)
val RedColor = Color(0xFFFE554A)

val gray = Color(0xFFf1f1f1)
val grayNight = Color(0xFF303030)

val grayText = Color(0xFF111111)
val grayTextNight = Color(0xFFffffff)

val background_content_dark = Color(0xFF222222)
val background_content_light = Color(0xFFEEEEEE)

val darkBackground = Color(0xFF121212)

val SentBubbleColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) blue else blueNight

val ReceivedBubbleColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) gray else grayNight

val ReceivedTextColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) grayText else grayTextNight

val BackgroundColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) blueBG else darkBackground

val TextColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) blueText else Color.White

val CardColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) card else Color.Black

val ButtonColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) blue else blueNight

val ButtonTextColor: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) Color.White else Color.Black

val BottomSheetBackground: Color
    @Composable
    get() = if (isSystemInDarkTheme()) background_content_dark else background_content_light

val DialogNoText: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) blue else Color.White

val SwitcherBackground: Color
    @Composable
    get() = if (!isSystemInDarkTheme()) Color.White else Color.Black