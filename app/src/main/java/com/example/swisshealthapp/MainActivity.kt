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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SwissHealthAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

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
        NavHost(
            navController = navController,
            startDestination = Screen.DAILY_GOALS.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.DAILY_GOALS.name) { DailyGoalsScreen() }
            composable(Screen.STATS.name) { StatsScreen() }
            composable(Screen.SETTINGS.name) { SettingsScreen() }
            composable(Screen.LANGUAGE.name) { LanguageScreen() }
            composable(Screen.DONATION.name) { DonationScreen() }
        }
    }
}

fun getIconForScreen(screen: Screen): Int {
    return when (screen) {
        Screen.DAILY_GOALS -> R.drawable.ic_goals
        Screen.STATS -> R.drawable.ic_stats
        Screen.SETTINGS -> R.drawable.ic_settings
        Screen.LANGUAGE -> R.drawable.ic_language
        Screen.DONATION -> R.drawable.ic_donation
    }
}

fun getLabelForScreen(screen: Screen): String {
    return when (screen) {
        Screen.DAILY_GOALS -> "tab_goals"
        Screen.STATS -> "tab_stats"
        Screen.SETTINGS -> "tab_settings"
        Screen.LANGUAGE -> "tab_language"
        Screen.DONATION -> "tab_donation"
    }
}