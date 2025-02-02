/**
 * Écran des statistiques de l'application Swiss Health
 * 
 * Cet écran affiche les tendances des points sur les 10 derniers jours, incluant :
 * - Un graphique comparatif entre les objectifs et les résultats
 * - Des statistiques sommaires (aujourd'hui, moyenne, maximum) pour les objectifs et les résultats
 * - Une légende pour différencier les séries de données
 * 
 * Le graphique utilise la bibliothèque Vico pour le rendu des données
 * L'interface est organisée en cartes distinctes pour une meilleure lisibilité
 * Les données sont mises à jour automatiquement via le StatsViewModel
 */

package com.example.swisshealthapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.example.swisshealthapp.viewmodel.StatsViewModel
import com.example.swisshealthapp.ui.components.LocalizedText
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/**
 * Composant principal de l'écran des statistiques
 * Organise l'affichage des données statistiques en plusieurs sections :
 * - Titre de la section
 * - Cartes des statistiques pour les objectifs et les résultats
 * - Graphique comparatif avec légende
 * 
 * @param viewModel ViewModel gérant les données statistiques
 */
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel()
) {
    val goalsPoints by viewModel.goalsPoints.collectAsState()
    val resultsPoints by viewModel.resultsPoints.collectAsState()
    val today = remember { LocalDate.now() }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM", Locale.FRENCH) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LocalizedText(
            text = "last_ten_days_points",
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (goalsPoints.isNotEmpty() && resultsPoints.isNotEmpty()) {
            // Carte des statistiques des objectifs
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    LocalizedText(
                        text = "goals_stats",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            title = "today_stats",
                            value = "${goalsPoints.last()} pts"
                        )
                        StatItem(
                            title = "average",
                            value = "${goalsPoints.average().toInt()} pts"
                        )
                        StatItem(
                            title = "maximum",
                            value = "${goalsPoints.maxOrNull() ?: 0} pts"
                        )
                    }
                }
            }

            // Carte des statistiques des résultats
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    LocalizedText(
                        text = "results_stats",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.semantics {
                            contentDescription = "section_title_results_stats"
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            title = "today_stats",
                            value = "${resultsPoints.last()} pts"
                        )
                        StatItem(
                            title = "average",
                            value = "${resultsPoints.average().toInt()} pts"
                        )
                        StatItem(
                            title = "maximum",
                            value = "${resultsPoints.maxOrNull() ?: 0} pts"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Carte du graphique comparatif
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(vertical = 8.dp)
            ) {
                // Préparation des données pour le graphique
                val goalsEntries = goalsPoints.mapIndexed { index, points ->
                    FloatEntry(index.toFloat(), points.toFloat())
                }
                val resultsEntries = resultsPoints.mapIndexed { index, points ->
                    FloatEntry(index.toFloat(), points.toFloat())
                }
                val chartEntryModel = ChartEntryModelProducer(listOf(goalsEntries, resultsEntries)).getModel()
                
                // Configuration et affichage du graphique
                Chart(
                    chart = lineChart(
                        lines = listOf(
                            lineSpec(
                                lineColor = MaterialTheme.colorScheme.primary,
                                lineThickness = 4.dp
                            ),
                            lineSpec(
                                lineColor = MaterialTheme.colorScheme.secondary,
                                lineThickness = 4.dp
                            )
                        )
                    ),
                    model = chartEntryModel ?: entryModelOf(listOf<FloatEntry>()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    startAxis = rememberStartAxis(
                        title = "points",
                        valueFormatter = { value, _ -> 
                            if (value >= 0 && value == value.toInt().toFloat()) {
                                value.toInt().toString()
                            } else {
                                ""
                            }
                        }
                    ),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = { value, _ ->
                            val date = today.minusDays(9 - value.toInt().toLong())
                            date.format(dateFormatter)
                        }
                    )
                )

                // Légende du graphique
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Indicateur pour les objectifs
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.small
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        LocalizedText(
                            text = "goals_stats",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Indicateur pour les résultats
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = MaterialTheme.shapes.small
                                )
                                .semantics {
                                    contentDescription = "graph_legend_results"
                                }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        LocalizedText(
                            text = "results_stats",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.semantics {
                                contentDescription = "graph_legend_label_results"
                            }
                        )
                    }
                }
            }
        } else {
            // Indicateur de chargement
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

/**
 * Composant affichant un élément statistique individuel
 * Organise verticalement un titre traduit et une valeur numérique
 * 
 * @param title Clé de traduction pour le titre de la statistique
 * @param value Valeur à afficher (ex: "42 pts")
 */
@Composable
fun StatItem(
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalizedText(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}