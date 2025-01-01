package com.example.swisshealthapp.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.swisshealthapp.data.GoalsRepository
import com.example.swisshealthapp.model.Goal
import kotlinx.coroutines.launch

class DailyGoalsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GoalsRepository(application)
    private val _goals = mutableStateListOf<Goal>()
    val goals: List<Goal> = _goals

    init {
        // Ajout des objectifs par défaut
        val defaultGoals = listOf(
            Goal(
                id = 1,
                title = "Boire 2L d'eau",
                points = 2,
                details = "Boire régulièrement dans la journée. Conseils : Gardez une bouteille d'eau à portée de main. Bénéfices : Hydratation, concentration, santé."
            ),
            Goal(
                id = 2,
                title = "30 minutes d'exercice",
                points = 3,
                details = "Faire de l'exercice physique pendant 30 minutes. Conseils : Choisissez une activité que vous aimez. Bénéfices : Santé cardiovasculaire, bien-être mental."
            ),
            Goal(
                id = 3,
                title = "8 heures de sommeil",
                points = 5,
                details = "Dormir 8 heures dans la nuit. Conseils : Maintenez un horaire de sommeil régulier. Bénéfices : Récupération, concentration, système immunitaire."
            )
        )
        
        _goals.addAll(defaultGoals)
        
        // Charger l'état de complétion pour chaque objectif
        defaultGoals.forEach { goal ->
            viewModelScope.launch {
                repository.getGoalCompletionStatus(goal.id).collect { isCompleted ->
                    val index = _goals.indexOfFirst { it.id == goal.id }
                    if (index != -1) {
                        _goals[index] = _goals[index].copy(isCompleted = isCompleted)
                    }
                }
            }
        }
    }

    fun toggleGoalCompletion(goalId: Int) {
        val index = _goals.indexOfFirst { it.id == goalId }
        if (index != -1) {
            val newStatus = !_goals[index].isCompleted
            _goals[index] = _goals[index].copy(isCompleted = newStatus)
            
            // Sauvegarder le nouvel état
            viewModelScope.launch {
                repository.saveGoalCompletionStatus(goalId, newStatus)
            }
        }
    }
} 