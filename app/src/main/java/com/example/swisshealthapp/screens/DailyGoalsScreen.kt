package com.example.swisshealthapp.screens

/**
 * Écran des objectifs quotidiens de l'application Swiss Health
 * 
 * Cet écran permet de :
 * - Visualiser et gérer les objectifs quotidiens de santé
 * - Naviguer entre différentes dates via un système de pagination horizontale
 * - Marquer les objectifs comme complétés ou non
 * - Voir les détails de chaque objectif dans une boîte de dialogue
 * 
 * L'écran utilise un HorizontalPager pour permettre la navigation fluide entre les dates
 */

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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/**
 * Composant principal de l'écran des objectifs quotidiens
 * Gère la navigation entre les dates et l'affichage des objectifs
 * 
 * @param viewModel ViewModel gérant les données des objectifs
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DailyGoalsScreen(
    viewModel: DailyGoalsViewModel = viewModel()
) {
    var selectedGoal by remember { mutableStateOf<Goal?>(null) }
    val pagerState = rememberPagerState(
        initialPage = 30,  // Page centrale représentant aujourd'hui
        pageCount = { 61 } // 30 jours avant et après aujourd'hui
    )
    val coroutineScope = rememberCoroutineScope()
    val today = remember { LocalDate.now() }
    val goalsMap by viewModel.goalsWithStatus.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Bouton pour revenir à aujourd'hui
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

        // Système de pagination horizontale
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

    // Affichage de la boîte de dialogue des détails si un objectif est sélectionné
    selectedGoal?.let { goal ->
        GoalDetailsDialog(
            goal = goal,
            onDismiss = { selectedGoal = null }
        )
    }
}

/**
 * Affiche le contenu principal pour une date donnée
 * Inclut l'en-tête avec la date et la liste des objectifs
 * 
 * @param date Date sélectionnée
 * @param goals Liste des objectifs pour la date
 * @param onGoalClick Callback lors du clic sur la case à cocher
 * @param onGoalDetails Callback pour afficher les détails
 */
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

/**
 * Affiche l'en-tête de la page des objectifs
 * Contient la date et la progression des points
 * 
 * @param date Date à afficher
 * @param goals Liste des objectifs pour calculer les points
 */
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

/**
 * Boîte de dialogue affichant les détails d'un objectif
 * Inclut le titre, les points et la description détaillée
 * 
 * @param goal Objectif dont on affiche les détails
 * @param onDismiss Callback pour fermer la boîte de dialogue
 */
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

/**
 * Affiche un objectif individuel sous forme de carte
 * Permet de marquer l'objectif comme complété et d'accéder aux détails
 * 
 * @param goal Objectif à afficher
 * @param onGoalClick Callback lors du clic sur la case à cocher
 * @param onGoalDetails Callback pour afficher les détails
 */
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
                onCheckedChange = { onGoalClick() },
                modifier = Modifier.semantics {
                    contentDescription = if (goal.isCompleted) {
                        "Objectif ${goal.title} coché"
                    } else {
                        "Objectif ${goal.title} non coché"
                    }
                }
            )
        }
    }
} 