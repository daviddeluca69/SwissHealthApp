package com.example.swisshealthapp.screens

import androidx.compose.foundation.layout.*
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
    val pointsData by viewModel.dailyPoints.collectAsState()
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
        
        if (pointsData.isNotEmpty()) {
            // Statistiques sommaires
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        title = "today_stats",
                        value = "${pointsData.last()} pts"
                    )
                    StatItem(
                        title = "average",
                        value = "${pointsData.average().toInt()} pts"
                    )
                    StatItem(
                        title = "maximum",
                        value = "${pointsData.maxOrNull() ?: 0} pts"
                    )
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
                val entries = pointsData.mapIndexed { index, points ->
                    FloatEntry(index.toFloat(), points.toFloat())
                }
                val chartEntryModel = ChartEntryModelProducer(entries).getModel()
                
                Chart(
                    chart = lineChart(),
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