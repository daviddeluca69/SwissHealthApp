package com.example.swisshealthapp.viewmodel

import android.app.Application
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
    private val _dailyNote = MutableStateFlow("")
    val resultsWithStatus: StateFlow<Map<LocalDate, List<Goal>>> = _resultsWithStatus
    val dailyNote: StateFlow<String> = _dailyNote

    init {
        viewModelScope.launch {
            repository.results.collect { results ->
                updateAllVisibleDates(results)
            }
        }

        viewModelScope.launch {
            repository.getResultsCompletionFlow()
                .onEach { _ -> 
                    val currentResults = repository.results.first()
                    updateAllVisibleDates(currentResults)
                }
                .collect()
        }

        viewModelScope.launch {
            _currentDate.collect { date ->
                updateDailyNote(date)
            }
        }
    }

    private suspend fun updateDailyNote(date: LocalDate) {
        val dateStr = date.format(DateTimeFormatter.ISO_DATE)
        _dailyNote.value = ""
        try {
            val note = repository.getDailyNote(dateStr).first()
            _dailyNote.value = note
        } catch (e: Exception) {
            _dailyNote.value = ""
        }
    }

    private suspend fun updateAllVisibleDates(results: List<Goal>) {
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
    }

    fun setCurrentDate(date: LocalDate) {
        _currentDate.value = date
    }

    fun saveDailyNote(note: String) {
        viewModelScope.launch {
            val dateStr = _currentDate.value.format(DateTimeFormatter.ISO_DATE)
            repository.saveDailyNote(dateStr, note)
            _dailyNote.value = note
        }
    }

    fun toggleResultCompletion(resultId: Int, date: LocalDate) {
        viewModelScope.launch {
            val dateStr = date.format(DateTimeFormatter.ISO_DATE)
            val currentStatus = repository.getResultCompletionStatus(resultId, dateStr).first()
            repository.updateResultCompletion(resultId, dateStr, !currentStatus)
            
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