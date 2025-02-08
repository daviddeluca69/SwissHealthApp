package com.example.swisshealthapp.ui.theme

/**
 * Définition des couleurs de l'application Swiss Health
 * 
 * Ce fichier définit :
 * - Les couleurs principales de l'identité visuelle suisse
 * - Les variantes de ces couleurs pour différents usages
 * - Les codes hexadécimaux précis pour chaque couleur
 */

import androidx.compose.ui.graphics.Color

/**
 * Rouge suisse officiel (version accessible)
 * Couleur principale de l'application
 * Code hexadécimal : #9B0000 (rapport de contraste > 4.5:1)
 */
val SwissRed = Color(0xFF9B0000)

/**
 * Blanc suisse officiel
 * Utilisé pour les fonds et les contrastes
 * Code hexadécimal : #FFFFFF
 */
val SwissWhite = Color(0xFFFFFFFF)

/**
 * Variante foncée du rouge suisse
 * Utilisée pour les éléments secondaires et les surbrillances
 * Code hexadécimal : #800000 (rapport de contraste > 7:1)
 */
val SwissRedDark = Color(0xFF800000)

/**
 * Variante claire du rouge suisse
 * Utilisée pour les éléments tertiaires et les états désactivés
 * Code hexadécimal : #D32F2F (rapport de contraste > 4.5:1)
 */
val SwissRedLight = Color(0xFFD32F2F)

/**
 * Couleur de texte principale
 * Utilisée pour le texte standard avec un contraste élevé
 * Code hexadécimal : #1C1B1F (rapport de contraste > 4.5:1 sur fond clair)
 */
val TextPrimary = Color(0xFF1C1B1F)

/**
 * Couleur de fond principale
 * Utilisée pour les surfaces avec un contraste suffisant
 * Code hexadécimal : #FFFFFF
 */
val BackgroundPrimary = Color(0xFFFFFFFF)

/**
 * Couleur de texte secondaire
 * Utilisée pour le texte moins important
 * Code hexadécimal : #49454F (rapport de contraste > 4.5:1 sur fond clair)
 */
val TextSecondary = Color(0xFF49454F)

/**
 * Couleur de texte pour les champs de saisie
 * Utilisée pour le texte des EditText avec un contraste optimal
 * Code hexadécimal : #1D1B20 (rapport de contraste > 4.5:1 sur fond clair)
 */
val InputTextColor = Color(0xFF1D1B20)

/**
 * Couleur de fond pour les champs de saisie
 * Utilisée pour le fond des EditText
 * Code hexadécimal : #FFFFFF
 */
val InputBackgroundColor = Color(0xFFFFFFFF)

/**
 * Couleur pour le texte d'indication (placeholder)
 * Utilisée pour les indications dans les champs de saisie
 * Code hexadécimal : #49454F (rapport de contraste > 4.5:1)
 */
val PlaceholderColor = Color(0xFF49454F)

/**
 * Couleur de texte à contraste élevé
 * Utilisée pour garantir une excellente lisibilité
 * Code hexadécimal : #000000 (rapport de contraste > 21:1 sur fond blanc)
 */
val HighContrastText = Color(0xFF000000)