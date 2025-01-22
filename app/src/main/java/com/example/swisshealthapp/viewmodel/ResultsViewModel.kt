package com.example.swisshealthapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.swisshealthapp.data.ResultsRepository
import com.example.swisshealthapp.model.Goal
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ResultsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ResultsRepository(application)
    private val _currentDate = MutableStateFlow(LocalDate.now())
    private val _resultsWithStatus = MutableStateFlow<Map<LocalDate, List<Goal>>>(emptyMap())
    val resultsWithStatus: StateFlow<Map<LocalDate, List<Goal>>> = _resultsWithStatus

    init {
        Log.d("ResultsViewModel", "Initialisation du ViewModel")
        viewModelScope.launch {
            Log.d("ResultsViewModel", "Début de la collecte des résultats")
            repository.results.collect { results ->
                Log.d("ResultsViewModel", "Nouveaux résultats reçus: ${results.size} résultats")
                updateAllVisibleDates(results)
            }
        }

        // Surveiller les changements dans les données du DataStore des résultats
        viewModelScope.launch {
            repository.getResultsCompletionFlow()
                .onEach { _ -> 
                    val currentResults = repository.results.first()
                    updateAllVisibleDates(currentResults)
                }
                .collect()
        }
    }

    private suspend fun updateAllVisibleDates(results: List<Goal>) {
        Log.d("ResultsViewModel", "Mise à jour des dates visibles avec ${results.size} résultats")
        val today = LocalDate.now()
        val visibleDates = (-30..30).map { today.plusDays(it.toLong()) }
        val newResultsMap = mutableMapOf<LocalDate, List<Goal>>()
        
        visibleDates.forEach { date ->
            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            val resultsWithStatus = results.map { result ->
                val isCompleted = repository.getResultCompletionStatus(result.id, dateStr).first()
                result.copy(isCompleted = isCompleted)
            }
            newResultsMap[date] = resultsWithStatus
        }
        
        _resultsWithStatus.value = newResultsMap
        Log.d("ResultsViewModel", "Mise à jour terminée, ${newResultsMap.size} dates mises à jour")
    }

    fun setCurrentDate(date: LocalDate) {
        _currentDate.value = date
    }

    fun toggleResultCompletion(resultId: Int, date: LocalDate) {
        viewModelScope.launch {
            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            val currentStatus = repository.getResultCompletionStatus(resultId, dateStr).first()
            repository.updateResultCompletion(resultId, dateStr, !currentStatus)
            
            // Mettre à jour l'état local
            val currentResults = _resultsWithStatus.value
            val updatedResults = currentResults[date]?.map { result ->
                if (result.id == resultId) {
                    result.copy(isCompleted = !currentStatus)
                } else {
                    result
                }
            }
            if (updatedResults != null) {
                _resultsWithStatus.value = currentResults + (date to updatedResults)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.onCleared()
    }
} 