package com.example.swisshealthapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.swisshealthapp.model.Settings
import com.example.swisshealthapp.model.Goal
import com.example.swisshealthapp.data.GoalsRepository

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GoalsRepository(application)
    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings
    val goals = repository.goals

    init {
        viewModelScope.launch {
            repository.initializeIfNeeded()
        }
    }

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

    fun updateDailyNotification(enabled: Boolean) {
        _settings.value = _settings.value.copy(dailyNotification = enabled)
    }

    fun updateDarkTheme(enabled: Boolean) {
        _settings.value = _settings.value.copy(darkTheme = enabled)
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            val currentGoals = goals.first()
            repository.saveGoals(currentGoals.filter { it.id != id })
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.onCleared()
    }
} 