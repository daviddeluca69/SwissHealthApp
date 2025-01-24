/**
 * ViewModel gérant les résultats quotidiens de l'application Swiss Health
 * 
 * Cette classe est responsable de :
 * - La gestion des résultats quotidiens et leur état de complétion
 * - Le suivi des notes quotidiennes
 * - La synchronisation avec le repository pour la persistance
 * - La gestion d'une fenêtre glissante de 61 jours (-30 à +30 jours)
 * 
 * Elle utilise des StateFlow pour exposer les données de manière réactive
 * et maintient une fenêtre glissante de dates pour optimiser les performances
 */

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
    /**
     * Repository pour la persistance des résultats et notes
     */
    private val repository = ResultsRepository(application)

    /**
     * Date actuellement sélectionnée dans l'interface
     * Utilisée pour charger les résultats et notes correspondants
     */
    private val _currentDate = MutableStateFlow(LocalDate.now())

    /**
     * Map associant chaque date à sa liste de résultats avec leur état
     * Permet un accès rapide aux résultats d'une date donnée
     */
    private val _resultsWithStatus = MutableStateFlow<Map<LocalDate, List<Goal>>>(emptyMap())

    /**
     * Note quotidienne pour la date sélectionnée
     */
    private val _dailyNote = MutableStateFlow("")

    val resultsWithStatus: StateFlow<Map<LocalDate, List<Goal>>> = _resultsWithStatus
    val dailyNote: StateFlow<String> = _dailyNote

    init {
        // Observe les changements dans la liste des résultats
        viewModelScope.launch {
            repository.results.collect { results ->
                updateAllVisibleDates(results)
            }
        }

        // Observe les changements dans l'état de complétion des résultats
        viewModelScope.launch {
            repository.getResultsCompletionFlow()
                .onEach { _ -> 
                    val currentResults = repository.results.first()
                    updateAllVisibleDates(currentResults)
                }
                .collect()
        }

        // Observe les changements de date pour mettre à jour la note quotidienne
        viewModelScope.launch {
            _currentDate.collect { date ->
                updateDailyNote(date)
            }
        }
    }

    /**
     * Met à jour la note quotidienne pour une date donnée
     * Récupère la note depuis le repository et gère les erreurs
     * 
     * @param date Date pour laquelle récupérer la note
     */
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

    /**
     * Met à jour l'état des résultats pour toutes les dates visibles
     * Gère une fenêtre glissante de 61 jours (-30 à +30 jours)
     * 
     * @param results Liste des résultats à mettre à jour
     */
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

    /**
     * Change la date actuellement sélectionnée
     * Déclenche la mise à jour de la note quotidienne
     * 
     * @param date Nouvelle date à sélectionner
     */
    fun setCurrentDate(date: LocalDate) {
        _currentDate.value = date
    }

    /**
     * Sauvegarde une nouvelle note pour la date actuelle
     * 
     * @param note Texte de la note à sauvegarder
     */
    fun saveDailyNote(note: String) {
        viewModelScope.launch {
            val dateStr = _currentDate.value.format(DateTimeFormatter.ISO_DATE)
            repository.saveDailyNote(dateStr, note)
            _dailyNote.value = note
        }
    }

    /**
     * Bascule l'état de complétion d'un résultat pour une date donnée
     * Met à jour à la fois le repository et l'état local
     * 
     * @param resultId Identifiant du résultat à modifier
     * @param date Date pour laquelle modifier l'état
     */
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

    /**
     * Libère les ressources du ViewModel et du repository
     */
    override fun onCleared() {
        super.onCleared()
        repository.onCleared()
    }
} 