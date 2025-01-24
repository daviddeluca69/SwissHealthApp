/**
 * Activité principale de l'application Swiss Health
 * 
 * Cette activité est responsable de :
 * - L'initialisation de l'application et de son interface utilisateur
 * - La configuration du thème et du support edge-to-edge
 * - La mise en place de la navigation entre les différents écrans
 * - L'affichage de la barre de navigation inférieure avec les icônes et labels traduits
 * 
 * L'application utilise Jetpack Compose pour l'interface utilisateur et
 * la navigation entre les écrans est gérée via une NavHost avec restauration d'état
 */

package com.example.swisshealthapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.swisshealthapp.navigation.Screen
import com.example.swisshealthapp.screens.*
import com.example.swisshealthapp.ui.theme.SwissHealthAppTheme
import com.example.swisshealthapp.ui.components.LocalizedText

/**
 * Activité principale servant de point d'entrée à l'application
 * Configure l'interface et initialise la navigation
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Active le support edge-to-edge pour une meilleure utilisation de l'écran
        setContent {
            SwissHealthAppTheme {
                MainScreen()
            }
        }
    }
}

/**
 * Écran principal de l'application
 * Gère la navigation entre les différentes sections via une barre de navigation inférieure
 * 
 * Fonctionnalités :
 * - Navigation fluide entre les écrans
 * - Restauration de l'état des écrans lors de la navigation
 * - Barre de navigation avec icônes et labels traduits
 * - Support du mode edge-to-edge avec padding approprié
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Création des items de navigation pour chaque écran
                Screen.entries.forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                painter = painterResource(id = getIconForScreen(screen)),
                                contentDescription = null
                            )
                        },
                        label = { LocalizedText(text = getLabelForScreen(screen)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.name } == true,
                        onClick = {
                            navController.navigate(screen.name) {
                                // Restaure l'état de l'écran lors de la navigation
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Configuration du graphe de navigation
        NavHost(
            navController = navController,
            startDestination = Screen.DAILY_GOALS.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.DAILY_GOALS.name) { DailyGoalsScreen() }
            composable(Screen.STATS.name) { StatsScreen() }
            composable(Screen.SETTINGS.name) { SettingsScreen() }
            composable(Screen.RESULTS.name) { ResultsScreen() }
            composable(Screen.DONATION.name) { DonationScreen() }
        }
    }
}

/**
 * Récupère l'icône correspondant à un écran donné
 * Chaque écran possède une icône distinctive dans les ressources drawables
 * 
 * @param screen Écran dont on veut l'icône
 * @return Identifiant de la ressource drawable de l'icône
 */
fun getIconForScreen(screen: Screen): Int {
    return when (screen) {
        Screen.DAILY_GOALS -> R.drawable.ic_goals
        Screen.STATS -> R.drawable.ic_stats
        Screen.SETTINGS -> R.drawable.ic_settings
        Screen.RESULTS -> R.drawable.ic_results
        Screen.DONATION -> R.drawable.ic_donation
    }
}

/**
 * Récupère la clé de traduction pour le label d'un écran
 * Ces clés sont utilisées avec le système de localisation pour afficher
 * les labels dans la langue sélectionnée par l'utilisateur
 * 
 * @param screen Écran dont on veut le label
 * @return Clé de traduction pour le label
 */
fun getLabelForScreen(screen: Screen): String {
    return when (screen) {
        Screen.DAILY_GOALS -> "tab_goals"
        Screen.STATS -> "tab_stats"
        Screen.SETTINGS -> "tab_settings"
        Screen.RESULTS -> "tab_results"
        Screen.DONATION -> "tab_donation"
    }
}