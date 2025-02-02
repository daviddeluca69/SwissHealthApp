package com.example.swisshealthapp.screens

/**
 * Écran des résultats quotidiens de l'application Swiss Health
 * 
 * Cet écran permet de :
 * - Visualiser et gérer les résultats quotidiens de santé
 * - Naviguer entre différentes dates via un système de pagination horizontale
 * - Ajouter des notes quotidiennes pour suivre son évolution
 * - Voir les détails de chaque résultat dans une boîte de dialogue
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swisshealthapp.model.Goal
import com.example.swisshealthapp.model.LocalizedStrings
import com.example.swisshealthapp.viewmodel.ResultsViewModel
import com.example.swisshealthapp.viewmodel.LanguageViewModel
import com.example.swisshealthapp.ui.components.LocalizedText
import com.example.swisshealthapp.ui.components.LocalizedTextWithParams
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/**
 * Composant principal de l'écran des résultats
 * Gère la navigation entre les dates et l'affichage des résultats quotidiens
 * 
 * @param viewModel ViewModel gérant les données des résultats
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ResultsScreen(
    viewModel: ResultsViewModel = viewModel()
) {
    var selectedResult by remember { mutableStateOf<Goal?>(null) }
    val pagerState = rememberPagerState(
        initialPage = 30,
        pageCount = { 61 }
    )
    val coroutineScope = rememberCoroutineScope()
    val today = remember { LocalDate.now() }
    val resultsMap by viewModel.resultsWithStatus.collectAsState()
    val currentDate = today.plusDays(pagerState.currentPage.toLong() - 30)
    val dailyNote by viewModel.dailyNote.collectAsState()

    LaunchedEffect(pagerState.currentPage) {
        val date = today.plusDays(pagerState.currentPage.toLong() - 30)
        viewModel.setCurrentDate(date)
    }

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
            val results = resultsMap[date] ?: emptyList()

            ResultsContent(
                date = date,
                results = results,
                dailyNote = if (date == currentDate) dailyNote else "",
                onResultClick = { resultId -> viewModel.toggleResultCompletion(resultId, date) },
                onResultDetails = { selectedResult = it },
                onNoteChange = { viewModel.saveDailyNote(it) }
            )
        }
    }

    selectedResult?.let { result ->
        ResultDetailsDialog(
            result = result,
            onDismiss = { selectedResult = null }
        )
    }
}

/**
 * Affiche le contenu principal pour une date donnée
 * Inclut l'en-tête avec la date, la note quotidienne et la liste des résultats
 * 
 * @param date Date sélectionnée
 * @param results Liste des résultats pour la date
 * @param dailyNote Note quotidienne associée
 * @param onResultClick Callback lors du clic sur un résultat
 * @param onResultDetails Callback pour afficher les détails d'un résultat
 * @param onNoteChange Callback lors de la modification de la note
 */
@Composable
private fun ResultsContent(
    date: LocalDate,
    results: List<Goal>,
    dailyNote: String,
    onResultClick: (Int) -> Unit,
    onResultDetails: (Goal) -> Unit,
    onNoteChange: (String) -> Unit
) {
    Column {
        ResultsHeader(date = date, results = results)
        
        Spacer(modifier = Modifier.height(16.dp))

        DailyNoteSection(
            note = dailyNote,
            onNoteChange = onNoteChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(results) { result ->
                ResultItem(
                    result = result,
                    onResultClick = { onResultClick(result.id) },
                    onResultDetails = { onResultDetails(result) }
                )
            }
        }
    }
}

/**
 * Affiche l'en-tête de la page des résultats
 * Contient la date et la progression des points
 * 
 * @param date Date à afficher
 * @param results Liste des résultats pour calculer les points
 */
@Composable
private fun ResultsHeader(
    date: LocalDate,
    results: List<Goal>
) {
    val totalPoints = results.filter { it.isCompleted }.sumOf { it.points }
    val maxPoints = results.sumOf { it.points }
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
 * Section permettant d'ajouter et modifier la note quotidienne
 * 
 * @param note Contenu actuel de la note
 * @param onNoteChange Callback appelé lors de la modification de la note
 */
@Composable
private fun DailyNoteSection(
    note: String,
    onNoteChange: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedNote by remember(note) { mutableStateOf(note) }
    val languageViewModel: LanguageViewModel = viewModel()
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocalizedStrings.get("daily_note", currentLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                if (!isEditing) {
                    IconButton(onClick = { isEditing = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "edit_daily_note"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isEditing) {
                TextField(
                    value = editedNote,
                    onValueChange = { editedNote = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        LocalizedText(text = "enter_daily_note")
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { 
                        isEditing = false
                        editedNote = note // Reset to original note if cancelled
                    }) {
                        LocalizedText(text = "cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onNoteChange(editedNote)
                            isEditing = false
                        }
                    ) {
                        LocalizedText(text = "save")
                    }
                }
            } else {
                if (note.isNotEmpty()) {
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    Text(
                        text = LocalizedStrings.get("no_note_yet", currentLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Affiche un résultat individuel sous forme de carte
 * Permet de marquer le résultat comme complété et d'accéder aux détails
 * 
 * @param result Résultat à afficher
 * @param onResultClick Callback lors du clic sur la case à cocher
 * @param onResultDetails Callback pour afficher les détails
 */
@Composable
private fun ResultItem(
    result: Goal,
    onResultClick: () -> Unit,
    onResultDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onResultDetails)
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
                    text = result.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${result.points} points",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Checkbox(
                checked = result.isCompleted,
                onCheckedChange = { onResultClick() },
                modifier = Modifier.semantics {
                    contentDescription = if (result.isCompleted) {
                        "Résultat ${result.title} coché"
                    } else {
                        "Résultat ${result.title} non coché"
                    }
                }
            )
        }
    }
}

/**
 * Boîte de dialogue affichant les détails d'un résultat
 * Inclut le titre, la description et les points associés
 * 
 * @param result Résultat dont on affiche les détails
 * @param onDismiss Callback pour fermer la boîte de dialogue
 */
@Composable
private fun ResultDetailsDialog(
    result: Goal,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.details,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                LocalizedTextWithParams(
                    text = "points_format",
                    params = arrayOf(result.points, result.points),
                    style = MaterialTheme.typography.titleMedium
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