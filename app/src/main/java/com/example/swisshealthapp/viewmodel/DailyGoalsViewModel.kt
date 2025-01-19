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
    private val repository = GoalsRepository(application)
    private val _currentDate = MutableStateFlow(LocalDate.now())
    private val _goalsWithStatus = MutableStateFlow<Map<LocalDate, List<Goal>>>(emptyMap())
    val goalsWithStatus: StateFlow<Map<LocalDate, List<Goal>>> = _goalsWithStatus

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

    fun setCurrentDate(date: LocalDate) {
        _currentDate.value = date
        Log.d("DailyGoalsViewModel", "Nouvelle date sélectionnée: $date")
    }
} 