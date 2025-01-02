package com.example.swisshealthapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swisshealthapp.viewmodel.SettingsViewModel
import com.example.swisshealthapp.model.Goal

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    var showGoalDialog by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<Goal?>(null) }
    var goalToDelete by remember { mutableStateOf<Goal?>(null) }
    var showResetConfirmation by remember { mutableStateOf(false) }
    val goals by viewModel.goals.collectAsState(initial = emptyList())
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Paramètres",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Gestion des objectifs
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gestion des objectifs",
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(onClick = { 
                            editingGoal = null
                            showGoalDialog = true 
                        }) {
                            Icon(Icons.Default.Add, "Ajouter un objectif")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    goals.forEach { goal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = goal.title,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${goal.points} points",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Row {
                                IconButton(onClick = {
                                    editingGoal = goal
                                    showGoalDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, "Modifier")
                                }
                                IconButton(onClick = { goalToDelete = goal }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Supprimer",
                                        tint = MaterialTheme.colorScheme.error
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

            Spacer(modifier = Modifier.height(24.dp))

            // Section Réinitialisation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Réinitialisation",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Attention : cette action effacera toutes les données de l'application et restaurera les objectifs par défaut.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { showResetConfirmation = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Réinitialiser l'application")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Boîte de dialogue de confirmation de suppression d'un objectif
    goalToDelete?.let { goal ->
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = { Text("Supprimer l'objectif") },
            text = { Text("Êtes-vous sûr de vouloir supprimer l'objectif \"${goal.title}\" ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteGoal(goal.id)
                        goalToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Boîte de dialogue de confirmation de réinitialisation
    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("Réinitialiser l'application") },
            text = { 
                Text(
                    "Êtes-vous sûr de vouloir réinitialiser l'application ? Cette action effacera toutes vos données et ne pourra pas être annulée."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showResetConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Réinitialiser")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Boîte de dialogue d'édition d'objectif
    if (showGoalDialog) {
        GoalDialog(
            goal = editingGoal,
            onDismiss = { 
                showGoalDialog = false
                editingGoal = null
            },
            onSave = { title, points, details ->
                if (editingGoal != null) {
                    viewModel.updateGoal(editingGoal!!.id, title, points, details)
                } else {
                    viewModel.addGoal(title, points, details)
                }
                showGoalDialog = false
                editingGoal = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDialog(
    goal: Goal?,
    onDismiss: () -> Unit,
    onSave: (String, Int, String) -> Unit
) {
    var title by remember { mutableStateOf(goal?.title ?: "") }
    var points by remember { mutableStateOf(goal?.points?.toString() ?: "") }
    var details by remember { mutableStateOf(goal?.details ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (goal == null) "Nouvel objectif" else "Modifier l'objectif") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre de l'objectif") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = points,
                    onValueChange = { points = it.filter { char -> char.isDigit() } },
                    label = { Text("Points") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Détails") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && points.isNotBlank()) {
                        onSave(title, points.toInt(), details)
                    }
                }
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
} 