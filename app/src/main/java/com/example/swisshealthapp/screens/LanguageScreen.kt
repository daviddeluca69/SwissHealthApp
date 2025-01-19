package com.example.swisshealthapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swisshealthapp.model.Language
import com.example.swisshealthapp.viewmodel.LanguageViewModel

@Composable
fun LanguageScreen(
    viewModel: LanguageViewModel = viewModel()
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Langue / Language",
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .selectableGroup()
                    .padding(16.dp)
            ) {
                Language.entries.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = language == currentLanguage,
                                onClick = { viewModel.setLanguage(language) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = language == currentLanguage,
                            onClick = null
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = language.displayName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = when (language) {
                                    Language.FRENCH -> "Définir le français comme langue de l'application"
                                    Language.ENGLISH -> "Set English as application language"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    if (language != Language.entries.last()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Column {
            Text(
                text = when (currentLanguage) {
                    Language.FRENCH -> "La langue sélectionnée sera appliquée à l'ensemble de l'application."
                    Language.ENGLISH -> "The selected language will be applied to the entire application."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = when (currentLanguage) {
                    Language.FRENCH -> "Attention : le changement de langue réinitialisera tous les objectifs à leurs valeurs par défaut dans la nouvelle langue."
                    Language.ENGLISH -> "Warning: changing the language will reset all goals to their default values in the new language."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
} 