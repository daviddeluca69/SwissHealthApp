package com.example.swisshealthapp.ui.theme

/**
 * Configuration du thème de l'application Swiss Health
 * 
 * Ce fichier définit :
 * - Les schémas de couleurs pour les thèmes clair et sombre
 * - Le support des couleurs dynamiques de Material You
 * - L'application du thème à l'ensemble de l'application
 */

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

/**
 * Schéma de couleurs pour le thème sombre
 * Utilise les couleurs suisses comme base avec un contraste amélioré
 */
private val DarkColorScheme = darkColorScheme(
    primary = SwissRed,
    secondary = SwissRedDark,
    tertiary = SwissRedLight,
    background = Color(0xFF1C1B1F),
    onPrimary = SwissWhite,
    onSecondary = SwissWhite,
    onTertiary = Color.Black,
    onBackground = SwissWhite,
    surface = Color(0xFF1C1B1F),
    onSurface = SwissWhite,
    error = Color(0xFFBA1A1A),
    onError = SwissWhite,
    outline = Color(0xFFCAC4D0)
)

/**
 * Schéma de couleurs pour le thème clair
 * Utilise les couleurs suisses avec un contraste amélioré
 */
private val LightColorScheme = lightColorScheme(
    primary = SwissRed,
    secondary = SwissRedDark,
    tertiary = SwissRedLight,
    background = BackgroundPrimary,
    onPrimary = SwissWhite,
    onSecondary = SwissWhite,
    onTertiary = Color.Black,
    onBackground = TextPrimary,
    surface = BackgroundPrimary,
    onSurface = TextPrimary,
    surfaceVariant = InputBackgroundColor,
    onSurfaceVariant = InputTextColor,
    error = Color(0xFFBA1A1A),
    onError = SwissWhite,
    outline = PlaceholderColor
)

/**
 * Composant principal du thème de l'application
 * 
 * @param darkTheme Active le thème sombre si vrai
 * @param dynamicColor Active les couleurs dynamiques sur Android 12+
 * @param content Contenu de l'application à styliser
 */
@Composable
fun SwissHealthAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
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