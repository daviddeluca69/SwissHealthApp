/**
 * ViewModel gérant les statistiques de l'application
 * 
 * Cette classe est responsable de :
 * - Le calcul des statistiques sur les objectifs et résultats
 * - Le suivi des points sur les 10 derniers jours
 * - La synchronisation avec les repositories pour les données
 * - La mise à disposition des données pour les graphiques
 * 
 * Elle utilise des StateFlow pour exposer les données de manière réactive
 * et maintient un historique des 10 derniers jours pour les tendances
 */

package com.example.swisshealthapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.swisshealthapp.data.GoalsRepository
import com.example.swisshealthapp.data.ResultsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * Repositories pour accéder aux données des objectifs et résultats
     */
    private val goalsRepository = GoalsRepository(application)
    private val resultsRepository = ResultsRepository(application)
    
    /**
     * Points des objectifs sur les 10 derniers jours
     * Chaque élément représente le total des points pour un jour
     */
    private val _goalsPoints = MutableStateFlow<List<Int>>(emptyList())
    val goalsPoints: StateFlow<List<Int>> = _goalsPoints
    
    /**
     * Points des résultats sur les 10 derniers jours
     * Permet de comparer la progression avec les objectifs
     */
    private val _resultsPoints = MutableStateFlow<List<Int>>(emptyList())
    val resultsPoints: StateFlow<List<Int>> = _resultsPoints

    /**
     * Initialise le ViewModel et configure les collecteurs
     * - Observe les changements dans les objectifs
     * - Observe les changements dans les résultats
     * - Met à jour les points lors des modifications
     */
    init {
        viewModelScope.launch {
            goalsRepository.goals
                .onEach { _ -> updateGoalsPoints() }
                .collect()
        }
        
        viewModelScope.launch {
            resultsRepository.results
                .onEach { _ -> updateResultsPoints() }
                .collect()
        }

        // Surveiller les changements dans les données du DataStore des résultats
        viewModelScope.launch {
            resultsRepository.getResultsCompletionFlow()
                .onEach { _ -> updateResultsPoints() }
                .collect()
        }
    }

    /**
     * Met à jour les points des objectifs pour les 10 derniers jours
     * - Calcule le total des points pour chaque jour
     * - Prend en compte uniquement les objectifs complétés
     * - Stocke les résultats dans l'ordre chronologique
     */
    private suspend fun updateGoalsPoints() {
        val today = LocalDate.now()
        val points = mutableListOf<Int>()
        val goals = goalsRepository.goals.first()
        
        for (i in 9 downTo 0) {
            val date = today.minusDays(i.toLong())
            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            var totalPoints = 0
            
            for (goal in goals) {
                val isCompleted = goalsRepository.getGoalCompletionStatus(goal.id, dateStr).first()
                if (isCompleted) {
                    totalPoints += goal.points
                }
            }
            points.add(totalPoints)
        }
        
        _goalsPoints.value = points
    }

    /**
     * Met à jour les points des résultats pour les 10 derniers jours
     * - Calcule le total des points pour chaque jour
     * - Prend en compte uniquement les résultats complétés
     * - Permet la comparaison avec les objectifs
     */
    private suspend fun updateResultsPoints() {
        val today = LocalDate.now()
        val points = mutableListOf<Int>()
        val results = resultsRepository.results.first()
        
        for (i in 9 downTo 0) {
            val date = today.minusDays(i.toLong())
            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            var totalPoints = 0
            
            for (result in results) {
                val isCompleted = resultsRepository.getResultCompletionStatus(result.id, dateStr).first()
                if (isCompleted) {
                    totalPoints += result.points
                }
            }
            points.add(totalPoints)
        }
        
        _resultsPoints.value = points
    }
} 