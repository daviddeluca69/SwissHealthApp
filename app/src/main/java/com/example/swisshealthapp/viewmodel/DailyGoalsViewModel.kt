/**
 * ViewModel gérant les objectifs quotidiens de l'application
 * 
 * Ce ViewModel est responsable de :
 * - La gestion de l'état des objectifs quotidiens
 * - La synchronisation avec le repository pour la persistance
 * - Le suivi de la date courante
 * - La mise à jour des objectifs visibles
 * - Le calcul des points et de la progression
 * 
 * Il utilise des StateFlow pour exposer les données à l'UI de manière réactive
 * et gère une fenêtre glissante de 61 jours (30 jours avant et après la date courante)
 */

package com.example.swisshealthapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.swisshealthapp.data.GoalsRepository
import com.example.swisshealthapp.model.Goal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyGoalsViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * Repository pour la persistance des objectifs et leur état
     * Gère la sauvegarde et la récupération des données
     */
    private val repository = GoalsRepository(application)

    /**
     * Date actuellement sélectionnée dans l'interface
     * Utilisée pour charger les objectifs correspondants
     * Se met à jour lors de la navigation entre les dates
     */
    private val _currentDate = MutableStateFlow(LocalDate.now())

    /**
     * Map associant chaque date à sa liste d'objectifs avec leur état
     * Permet un accès rapide aux objectifs d'une date donnée
     * La clé est une date et la valeur est la liste des objectifs pour cette date
     */
    private val _goalsWithStatus = MutableStateFlow<Map<LocalDate, List<Goal>>>(emptyMap())
    
    /**
     * Version publique de la map des objectifs
     * Exposée à l'UI pour l'affichage des données
     */
    val goalsWithStatus: StateFlow<Map<LocalDate, List<Goal>>> = _goalsWithStatus

    /**
     * Initialise le ViewModel et configure les collecteurs de données
     * - Initialise le repository si nécessaire
     * - Configure l'observation des changements d'objectifs
     * - Met à jour les dates visibles lors des changements
     */
    init {
        Log.d("DailyGoalsViewModel", "Initialisation du ViewModel")
        viewModelScope.launch {
            repository.initializeIfNeeded()
            Log.d("DailyGoalsViewModel", "Début de la collecte des objectifs")
            repository.goals.collect { goals ->
                Log.d("DailyGoalsViewModel", "Nouveaux objectifs reçus: ${goals.size} objectifs")
                updateAllVisibleDates(goals)
            }
        }
    }

    /**
     * Met à jour l'état des objectifs pour toutes les dates visibles
     * - Charge l'état de complétion pour chaque objectif à chaque date
     * - Gère une fenêtre de 61 jours centrée sur aujourd'hui
     * - Met à jour la map des objectifs de manière atomique
     * 
     * @param goals Liste des objectifs à mettre à jour
     */
    private suspend fun updateAllVisibleDates(goals: List<Goal>) {
        Log.d("DailyGoalsViewModel", "Mise à jour des dates visibles avec ${goals.size} objectifs")
        val today = LocalDate.now()
        val visibleDates = (-30..30).map { today.plusDays(it.toLong()) }
        val newGoalsMap = mutableMapOf<LocalDate, List<Goal>>()
        
        visibleDates.forEach { date ->
            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            val goalsWithStatus = goals.map { goal ->
                val isCompleted = repository.getGoalCompletionStatus(goal.id, dateStr).first()
                goal.copy(isCompleted = isCompleted)
            }
            newGoalsMap[date] = goalsWithStatus
        }
        
        _goalsWithStatus.value = newGoalsMap
        Log.d("DailyGoalsViewModel", "Mise à jour terminée, ${newGoalsMap.size} dates mises à jour")
    }

    /**
     * Change l'état de complétion d'un objectif pour une date donnée
     * - Inverse l'état actuel de l'objectif
     * - Met à jour la persistance via le repository
     * - Rafraîchit l'état local pour l'UI
     * 
     * @param goalId Identifiant de l'objectif à modifier
     * @param date Date pour laquelle modifier l'état
     */
    fun toggleGoalCompletion(goalId: Int, date: LocalDate) {
        Log.d("DailyGoalsViewModel", "Toggle objectif $goalId pour la date $date")
        viewModelScope.launch {
            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            val currentStatus = repository.getGoalCompletionStatus(goalId, dateStr).first()
            repository.updateGoalCompletion(goalId, dateStr, !currentStatus)
            
            val currentGoals = repository.goals.first()
            val updatedGoals = currentGoals.map { goal ->
                val isCompleted = if (goal.id == goalId) !currentStatus 
                    else repository.getGoalCompletionStatus(goal.id, dateStr).first()
                goal.copy(isCompleted = isCompleted)
            }
            
            _goalsWithStatus.update { currentMap ->
                currentMap.toMutableMap().apply {
                    put(date, updatedGoals)
                }
            }
            Log.d("DailyGoalsViewModel", "Toggle terminé, nouveau statut: ${!currentStatus}")
        }
    }

    /**
     * Met à jour la date courante et déclenche le chargement des objectifs
     * - Stocke la nouvelle date sélectionnée
     * - Utilisé lors de la navigation entre les dates
     * 
     * @param date Nouvelle date sélectionnée
     */
    fun setCurrentDate(date: LocalDate) {
        _currentDate.value = date
        Log.d("DailyGoalsViewModel", "Nouvelle date sélectionnée: $date")
    }
} 