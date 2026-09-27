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

private val DarkColorScheme = darkColorScheme(
    primary = BloodRedDarkPrimary,
    onPrimary = BloodRedDarkOnPrimary,
    primaryContainer = BloodRedDarkContainer,
    onPrimaryContainer = BloodRedDarkOnContainer,
    secondary = MedicalDarkSecondary,
    secondaryContainer = MedicalDarkSecondaryContainer,
    onSecondaryContainer = MedicalDarkOnSecondaryContainer,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    outline = DarkOutline,
    error = Color(0xFFFFB4AB)
)

private val LightColorScheme = lightColorScheme(
    primary = BloodRedPrimary,
    onPrimary = BloodRedOnPrimary,
    primaryContainer = BloodRedContainer,
    onPrimaryContainer = BloodRedOnContainer,
    secondary = MedicalSecondary,
    secondaryContainer = MedicalSecondaryContainer,
    onSecondaryContainer = MedicalOnSecondaryContainer,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    outline = LightOutline,
    error = Color(0xFFBA1A1A)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set default false to preserve iconic medical red identity
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
