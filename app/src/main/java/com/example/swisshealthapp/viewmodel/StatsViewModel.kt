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
    private val goalsRepository = GoalsRepository(application)
    private val resultsRepository = ResultsRepository(application)
    
    private val _goalsPoints = MutableStateFlow<List<Int>>(emptyList())
    val goalsPoints: StateFlow<List<Int>> = _goalsPoints
    
    private val _resultsPoints = MutableStateFlow<List<Int>>(emptyList())
    val resultsPoints: StateFlow<List<Int>> = _resultsPoints

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