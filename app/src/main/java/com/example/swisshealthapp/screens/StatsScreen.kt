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
import com.example.swisshealthapp.viewmodel.StatsViewModel
import com.example.swisshealthapp.ui.components.LocalizedText
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
            // Statistiques sommaires des objectifs
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

            // Statistiques sommaires des résultats
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
                        style = MaterialTheme.typography.titleMedium
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

            // Graphique
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(vertical = 8.dp)
            ) {
                val goalsEntries = goalsPoints.mapIndexed { index, points ->
                    FloatEntry(index.toFloat(), points.toFloat())
                }
                val resultsEntries = resultsPoints.mapIndexed { index, points ->
                    FloatEntry(index.toFloat(), points.toFloat())
                }
                val chartEntryModel = ChartEntryModelProducer(listOf(goalsEntries, resultsEntries)).getModel()
                
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
                    model = chartEntryModel,
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

                // Légende
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Légende pour les objectifs
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

                    // Légende pour les résultats
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
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        LocalizedText(
                            text = "results_stats",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

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