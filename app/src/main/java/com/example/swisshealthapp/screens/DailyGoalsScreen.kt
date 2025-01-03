package com.example.swisshealthapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swisshealthapp.model.Goal
import com.example.swisshealthapp.viewmodel.DailyGoalsViewModel
import com.example.swisshealthapp.ui.components.LocalizedText
import com.example.swisshealthapp.ui.components.LocalizedTextWithParams
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DailyGoalsScreen(
    viewModel: DailyGoalsViewModel = viewModel()
) {
    var selectedGoal by remember { mutableStateOf<Goal?>(null) }
    val pagerState = rememberPagerState(
        initialPage = 30,
        pageCount = { 61 }
    )
    val coroutineScope = rememberCoroutineScope()
    val today = remember { LocalDate.now() }
    val goalsMap by viewModel.goalsWithStatus.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(30)
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            LocalizedText(text = "today")
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val date = today.plusDays(page.toLong() - 30)
            val goals = goalsMap[date] ?: emptyList()
            
            LaunchedEffect(date) {
                viewModel.setCurrentDate(date)
            }

            DailyContent(
                date = date,
                goals = goals,
                onGoalClick = { viewModel.toggleGoalCompletion(it, date) },
                onGoalDetails = { selectedGoal = it }
            )
        }
    }

    selectedGoal?.let { goal ->
        GoalDetailsDialog(
            goal = goal,
            onDismiss = { selectedGoal = null }
        )
    }
}

@Composable
fun DailyContent(
    date: LocalDate,
    goals: List<Goal>,
    onGoalClick: (Int) -> Unit,
    onGoalDetails: (Goal) -> Unit
) {
    Column {
        DailyHeader(date = date, goals = goals)
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(goals) { goal ->
                GoalItem(
                    goal = goal,
                    onGoalClick = { onGoalClick(goal.id) },
                    onGoalDetails = { onGoalDetails(goal) }
                )
            }
        }
    }
}

@Composable
fun DailyHeader(
    date: LocalDate,
    goals: List<Goal>
) {
    val totalPoints = goals.filter { it.isCompleted }.sumOf { it.points }
    val maxPoints = goals.sumOf { it.points }
    val formatter = remember { DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = date.format(formatter),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LocalizedTextWithParams(
            text = "points_format",
            params = arrayOf(totalPoints, maxPoints),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
        
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            progress = { if (maxPoints > 0) totalPoints.toFloat() / maxPoints else 0f }
        )
    }
}

@Composable
fun GoalDetailsDialog(
    goal: Goal,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                LocalizedText(
                    text = "goal_details",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = "${goal.points} points",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = goal.details,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    LocalizedText(text = "close")
                }
            }
        }
    }
}

@Composable
fun GoalItem(
    goal: Goal,
    onGoalClick: () -> Unit,
    onGoalDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onGoalDetails)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${goal.points} points",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Checkbox(
                checked = goal.isCompleted,
                onCheckedChange = { onGoalClick() }
            )
        }
    }
} 