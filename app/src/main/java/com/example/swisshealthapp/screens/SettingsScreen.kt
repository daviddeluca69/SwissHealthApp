package com.example.swisshealthapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swisshealthapp.model.Goal
import com.example.swisshealthapp.model.Language
import com.example.swisshealthapp.viewmodel.SettingsViewModel
import com.example.swisshealthapp.ui.components.LocalizedText
import com.example.swisshealthapp.ui.components.LocalizedTextWithParams
import com.example.swisshealthapp.viewmodel.LanguageViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    languageViewModel: LanguageViewModel = viewModel()
) {
    var showDeleteDialog by remember { mutableStateOf<Goal?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showAddEditDialog by remember { mutableStateOf<Goal?>(null) }
    
    val goals by viewModel.goals.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LocalizedText(
                text = "settings",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    LocalizedText(
                        text = "goals_management",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { showAddEditDialog = Goal(0, "", 0, "") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LocalizedText(text = "add_goal")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    goals.forEach { goal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = goal.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${goal.points} points",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row {
                                IconButton(onClick = { showAddEditDialog = goal }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Modifier l'objectif"
                                    )
                                }
                                IconButton(onClick = { showDeleteDialog = goal }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Supprimer l'objectif"
                                    )
                                }
                            }
                        }
                        if (goal != goals.last()) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        item {
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    LocalizedText(
                        text = "reset_section",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LocalizedText(
                        text = "reset_warning",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LocalizedText(text = "reset_app")
                    }
                }
            }
        }

        item {
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    LocalizedText(
                        text = "language_section",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
                    
                    Column(modifier = Modifier.selectableGroup()) {
                        Language.values().forEach { language ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = language == currentLanguage,
                                        onClick = { languageViewModel.setLanguage(language) },
                                        role = Role.RadioButton
                                    )
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = language == currentLanguage,
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = language.name)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "La langue sélectionnée sera appliquée à l'ensemble de l'application.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Attention : le changement de langue réinitialisera tous les objectifs à leurs valeurs par défaut dans la nouvelle langue.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // Dialogue de suppression
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { LocalizedText(text = "delete_goal") },
            text = { 
                LocalizedTextWithParams(
                    text = "delete_goal_confirmation",
                    params = arrayOf(showDeleteDialog!!.title)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog?.let { goal ->
                            viewModel.deleteGoal(goal.id)
                        }
                        showDeleteDialog = null
                    }
                ) {
                    LocalizedText(text = "confirm_delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    LocalizedText(text = "cancel")
                }
            }
        )
    }

    // Dialogue de réinitialisation
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { LocalizedText(text = "reset_app") },
            text = { LocalizedText(text = "reset_confirmation") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showResetDialog = false
                    }
                ) {
                    LocalizedText(text = "confirm_reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    LocalizedText(text = "cancel")
                }
            }
        )
    }

    // Dialogue d'ajout/modification d'objectif
    if (showAddEditDialog != null) {
        val goal = showAddEditDialog!!
        var title by remember { mutableStateOf(goal.title) }
        var points by remember { mutableStateOf(goal.points.toString()) }
        var details by remember { mutableStateOf(goal.details) }

        AlertDialog(
            onDismissRequest = { showAddEditDialog = null },
            title = {
                LocalizedText(
                    text = if (goal.id == 0) "new_goal" else "edit_goal_title"
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { LocalizedText(text = "goal_title_label") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = points,
                        onValueChange = { points = it },
                        label = { LocalizedText(text = "goal_points_label") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        label = { LocalizedText(text = "goal_details_label") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updatedGoal = Goal(
                            id = goal.id,
                            title = title,
                            points = points.toIntOrNull() ?: 0,
                            details = details
                        )
                        if (goal.id == 0) {
                            viewModel.addGoal(title, points.toIntOrNull() ?: 0, details)
                        } else {
                            viewModel.updateGoal(goal.id, title, points.toIntOrNull() ?: 0, details)
                        }
                        showAddEditDialog = null
                    }
                ) {
                    LocalizedText(text = "save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEditDialog = null }) {
                    LocalizedText(text = "cancel")
                }
            }
        )
    }
} 