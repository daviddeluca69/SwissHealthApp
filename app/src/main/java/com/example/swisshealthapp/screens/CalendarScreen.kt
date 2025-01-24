/**
 * Écran du calendrier de l'application Swiss Health
 * 
 * Cet écran est actuellement un composant placeholder qui affiche simplement
 * le texte "Calendrier" au centre de l'écran. Il est prévu pour une future
 * implémentation qui permettra de visualiser les données de santé sous forme
 * de calendrier mensuel.
 * 
 * Fonctionnalités prévues :
 * - Vue mensuelle des objectifs et résultats
 * - Navigation entre les mois
 * - Visualisation rapide des points quotidiens
 * - Accès aux détails d'une journée spécifique
 */

package com.example.swisshealthapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Composant principal de l'écran du calendrier
 * Actuellement, affiche uniquement un texte centré
 * En attente d'implémentation complète
 */
@Composable
fun CalendarScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Calendrier")
    }
} 