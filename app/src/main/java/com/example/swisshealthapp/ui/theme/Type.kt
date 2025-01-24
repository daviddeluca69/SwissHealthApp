package com.example.swisshealthapp.ui.theme

/**
 * Configuration de la typographie de l'application Swiss Health
 * 
 * Ce fichier définit :
 * - Les différents styles de texte selon Material Design 3
 * - Les tailles, poids et espacements des polices
 * - La hiérarchie typographique de l'application
 */

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Définition des styles typographiques de l'application
 * Basé sur les recommandations Material Design 3
 */
val Typography = Typography(
    /**
     * Style pour le corps de texte principal
     * Utilisé pour le contenu principal et les descriptions
     */
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    
    /* Autres styles disponibles à personnaliser :
    
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)