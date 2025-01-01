package com.example.swisshealthapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel()
) {
    val pointsData by viewModel.dailyPoints.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Points des 10 derniers jours",
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (pointsData.isNotEmpty()) {
            val entries = pointsData.mapIndexed { index, points ->
                FloatEntry(index.toFloat(), points.toFloat())
            }
            val chartEntryModel = ChartEntryModelProducer(entries).getModel()
            
            Chart(
                chart = lineChart(),
                model = chartEntryModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                startAxis = rememberStartAxis(
                    title = "Points",
                    valueFormatter = { value, _ -> 
                        if (value >= 0 && value == value.toInt().toFloat()) {
                            value.toInt().toString()
                        } else {
                            ""
                        }
                    }
                ),
                bottomAxis = rememberBottomAxis(
                    valueFormatter = { _, _ -> "" }
                )
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
            )
        }
    }
} 