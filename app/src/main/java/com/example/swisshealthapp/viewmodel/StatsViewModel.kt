package com.example.swisshealthapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.swisshealthapp.data.GoalsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GoalsRepository(application)
    
    private val _dailyPoints = MutableStateFlow<List<Int>>(emptyList())
    val dailyPoints: StateFlow<List<Int>> = _dailyPoints

    init {
        loadLastTenDaysPoints()
    }

    private fun loadLastTenDaysPoints() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val points = mutableListOf<Int>()
            val goals = repository.goals.first()
            
            for (i in 9 downTo 0) {
                val date = today.minusDays(i.toLong())
                val dateStr = date.format(DateTimeFormatter.ISO_DATE)
                var totalPoints = 0
                
                for (goal in goals) {
                    val isCompleted = repository.getGoalCompletionStatus(goal.id, dateStr).first()
                    if (isCompleted) {
                        totalPoints += goal.points
                    }
                }
                points.add(totalPoints)
            }
            
            _dailyPoints.value = points
        }
    }
} 