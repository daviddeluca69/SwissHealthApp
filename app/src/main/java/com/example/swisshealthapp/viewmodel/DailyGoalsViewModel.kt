package com.example.swisshealthapp.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.swisshealthapp.data.GoalsRepository
import com.example.swisshealthapp.model.Goal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class DailyGoalsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GoalsRepository(application)
    private val defaultGoals = listOf(
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

    private val _goalsStateMap = mutableStateMapOf<LocalDate, List<Goal>>()

    fun getGoalsForDate(date: LocalDate): List<Goal> {
        if (!_goalsStateMap.containsKey(date)) {
            viewModelScope.launch {
                val loadedGoals = loadGoalsForDate(date)
                _goalsStateMap[date] = loadedGoals
            }
            return defaultGoals.map { it.copy() }
        }
        return _goalsStateMap[date] ?: emptyList()
    }

    private suspend fun loadGoalsForDate(date: LocalDate): List<Goal> {
        return defaultGoals.map { goal ->
            val isCompleted = repository.getGoalCompletionStatus(goal.id, date).first()
            goal.copy(isCompleted = isCompleted)
        }
    }

    fun toggleGoalCompletion(goalId: Int, date: LocalDate) {
        val goals = _goalsStateMap[date] ?: defaultGoals.map { it.copy() }
        val goal = goals.find { it.id == goalId } ?: return
        val newStatus = !goal.isCompleted
        
        viewModelScope.launch {
            repository.saveGoalCompletionStatus(goalId, date, newStatus)
            val updatedGoals = goals.map { 
                if (it.id == goalId) it.copy(isCompleted = newStatus) else it 
            }
            _goalsStateMap[date] = updatedGoals
        }
    }
} 