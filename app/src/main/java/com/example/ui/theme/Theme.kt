package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize

private val ComedyDarkColorScheme = darkColorScheme(
    background = DarkCharcoalBg,
    surface = CardDarkBg,
    surfaceVariant = CardSurfaceVariant,
    primary = JoyYellow,
    secondary = JoyPink,
    tertiary = JoyOrange,
    onBackground = OnDarkTextPrimary,
    onSurface = OnDarkTextPrimary,
    onPrimary = DarkCharcoalBg,
    onSecondary = OnDarkTextPrimary
)

@Composable
fun ComedySuperAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ComedyDarkColorScheme,
        typography = Typography
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}
