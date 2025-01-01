package com.example.swisshealthapp.viewmodel

import android.app.Application
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
        viewModelScope.launch {
            repository.goals.collect { goals ->
                updateAllVisibleDates(goals)
            }
        }
    }

    private suspend fun updateAllVisibleDates(goals: List<Goal>) {
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
    }

    fun setCurrentDate(date: LocalDate) {
        _currentDate.value = date
    }

    fun toggleGoalCompletion(goalId: Int, date: LocalDate) {
        viewModelScope.launch {
            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            val currentStatus = repository.getGoalCompletionStatus(goalId, dateStr).first()
            repository.updateGoalCompletion(goalId, dateStr, !currentStatus)
            
            // Mettre à jour uniquement la date concernée
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
        }
    }
} 