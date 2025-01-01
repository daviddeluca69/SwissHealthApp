package com.example.swisshealthapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.swisshealthapp.data.GoalsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GoalsRepository(application)
    private val defaultGoals = DailyGoalsViewModel(application).getGoalsForDate(LocalDate.now())
    
    private val _dailyPoints = MutableStateFlow<List<Int>>(emptyList())
    val dailyPoints: StateFlow<List<Int>> = _dailyPoints

    private var updateJob: Job? = null

    init {
        startPeriodicUpdate()
    }

    private fun startPeriodicUpdate() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while(true) {
                loadLastTenDaysPoints()
                delay(1000) // Mettre à jour toutes les secondes
            }
        }
    }

    private suspend fun loadLastTenDaysPoints() {
        val today = LocalDate.now()
        val points = mutableListOf<Int>()
        
        for (i in 9 downTo 0) {
            val date = today.minusDays(i.toLong())
            val dailyPoints = repository.getDailyPoints(date, defaultGoals)
            points.add(dailyPoints)
        }
        
        _dailyPoints.value = points
    }

    override fun onCleared() {
        super.onCleared()
        updateJob?.cancel()
    }
} 