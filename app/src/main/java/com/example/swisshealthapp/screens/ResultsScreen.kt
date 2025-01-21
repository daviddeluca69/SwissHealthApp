package com.example.swisshealthapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.swisshealthapp.ui.components.LocalizedText

@Composable
fun ResultsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LocalizedText(
            text = "results_title",
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LocalizedText(
            text = "results_coming_soon",
            style = MaterialTheme.typography.bodyLarge
        )
    }
} 