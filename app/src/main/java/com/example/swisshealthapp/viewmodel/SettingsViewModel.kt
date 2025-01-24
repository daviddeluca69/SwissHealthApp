/**
 * ViewModel gérant les paramètres et objectifs de l'application
 * 
 * Cette classe est responsable de :
 * - La gestion des objectifs (ajout, modification, suppression)
 * - La réinitialisation des données de l'application
 * - La synchronisation avec les repositories pour la persistance
 * 
 * Elle utilise GoalsRepository pour les objectifs et ResultsRepository pour les résultats
 */

package com.example.swisshealthapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.swisshealthapp.model.Goal
import com.example.swisshealthapp.data.GoalsRepository
import com.example.swisshealthapp.data.ResultsRepository

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * Repository pour la gestion des objectifs
     */
    private val repository = GoalsRepository(application)

    /**
     * Repository pour la gestion des résultats
     */
    private val resultsRepository = ResultsRepository(application)

    /**
     * Flow exposant la liste des objectifs
     * Se met à jour automatiquement lors des modifications
     */
    val goals = repository.goals

    /**
     * Initialise le repository des objectifs au démarrage
     */
    init {
        viewModelScope.launch {
            repository.initializeIfNeeded()
        }
    }

    /**
     * Ajoute un nouvel objectif
     * 
     * @param title Titre de l'objectif
     * @param points Points associés à l'objectif
     * @param details Description détaillée de l'objectif
     */
    fun addGoal(title: String, points: Int, details: String) {
        viewModelScope.launch {
            val currentGoals = goals.first()
            val newGoal = Goal(
                id = (currentGoals.maxOfOrNull { it.id } ?: 0) + 1,
                title = title,
                points = points,
                details = details
            )
            repository.saveGoals(currentGoals + newGoal)
        }
    }

    /**
     * Met à jour un objectif existant
     * 
     * @param id Identifiant de l'objectif à modifier
     * @param title Nouveau titre
     * @param points Nouveaux points
     * @param details Nouvelle description
     */
    fun updateGoal(id: Int, title: String, points: Int, details: String) {
        viewModelScope.launch {
            val currentGoals = goals.first()
            val updatedGoals = currentGoals.map { goal ->
                if (goal.id == id) {
                    goal.copy(title = title, points = points, details = details)
                } else {
                    goal
                }
            }
            repository.saveGoals(updatedGoals)
        }
    }

    /**
     * Supprime un objectif
     * 
     * @param id Identifiant de l'objectif à supprimer
     */
    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            val currentGoals = goals.first()
            repository.saveGoals(currentGoals.filter { it.id != id })
        }
    }

    /**
     * Réinitialise toutes les données de l'application
     * Efface les objectifs et les résultats
     */
    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            resultsRepository.clearAllData()
        }
    }

    /**
     * Libère les ressources lors de la destruction du ViewModel
     */
    override fun onCleared() {
        super.onCleared()
        repository.onCleared()
    }
} 