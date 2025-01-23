package com.example.swisshealthapp.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.swisshealthapp.model.Goal
import com.example.swisshealthapp.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

private val Context.resultsDataStore: DataStore<Preferences> by preferencesDataStore(name = "results")

class ResultsRepository(private val context: Context) {
    private val TAG = "ResultsRepository"
    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true
    }
    private val languageRepository = LanguageRepository(context)
    private val _resultsFlow = MutableStateFlow<List<Goal>>(emptyList())
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private object PreferencesKeys {
        val RESULTS_COMPLETION = stringPreferencesKey("results_completion")
        val DAILY_NOTES = stringPreferencesKey("daily_notes")
    }

    private val defaultResultsFrench = listOf(
        Goal(
            id = 1,
            title = "Qualité du sommeil",
            points = 20,
            details = "Évaluation subjective de la qualité de votre sommeil"
        ),
        Goal(
            id = 2,
            title = "Niveau d'énergie",
            points = 20,
            details = "Évaluation subjective de votre niveau d'énergie dans la journée"
        ),
        Goal(
            id = 3,
            title = "Niveau de stress",
            points = 20,
            details = "Évaluation subjective de votre niveau de stress dans la journée"
        ),
        Goal(
            id = 4,
            title = "Humeur générale",
            points = 20,
            details = "Évaluation subjective de votre état émotionnel de la journée"
        ),
        Goal(
            id = 5,
            title = "Confort digestif",
            points = 20,
            details = "Évaluation subjective de votre confort digestif de la journée"
        )
    )

    private val defaultResultsEnglish = listOf(
        Goal(
            id = 1,
            title = "Sleep Quality",
            points = 20,
            details = "Subjective evaluation of your sleep quality"
        ),
        Goal(
            id = 2,
            title = "Energy Level",
            points = 20,
            details = "Subjective evaluation of your energy level during the day"
        ),
        Goal(
            id = 3,
            title = "Stress Level",
            points = 20,
            details = "Subjective evaluation of your stress level during the day"
        ),
        Goal(
            id = 4,
            title = "General Mood",
            points = 20,
            details = "Subjective evaluation of your emotional state during the day"
        ),
        Goal(
            id = 5,
            title = "Digestive Comfort",
            points = 20,
            details = "Subjective evaluation of your digestive comfort during the day"
        )
    )

    val results: Flow<List<Goal>> = languageRepository.currentLanguage.map { language ->
        when (language) {
            Language.FRENCH -> defaultResultsFrench
            Language.ENGLISH -> defaultResultsEnglish
        }.also { _resultsFlow.value = it }
    }

    init {
        Log.d(TAG, "Initialisation du ResultsRepository")
        scope.launch {
            languageRepository.currentLanguage.collect { language ->
                _resultsFlow.value = when (language) {
                    Language.FRENCH -> defaultResultsFrench
                    Language.ENGLISH -> defaultResultsEnglish
                }
            }
        }
    }

    fun onCleared() {
        scope.cancel()
    }

    suspend fun updateResultCompletion(goalId: Int, date: String, isCompleted: Boolean) {
        context.resultsDataStore.edit { preferences ->
            val completionJson = preferences[PreferencesKeys.RESULTS_COMPLETION] ?: "{}"
            val completionMap = try {
                json.decodeFromString<Map<String, Map<Int, Boolean>>>(completionJson).toMutableMap()
            } catch (e: Exception) {
                mutableMapOf()
            }
            
            val dateMap = completionMap.getOrDefault(date, mutableMapOf()).toMutableMap()
            dateMap[goalId] = isCompleted
            completionMap[date] = dateMap
            
            preferences[PreferencesKeys.RESULTS_COMPLETION] = json.encodeToString(completionMap)
        }
    }

    fun getResultCompletionStatus(goalId: Int, date: String): Flow<Boolean> {
        return context.resultsDataStore.data.map { preferences ->
            val completionJson = preferences[PreferencesKeys.RESULTS_COMPLETION] ?: "{}"
            try {
                val completionMap = json.decodeFromString<Map<String, Map<Int, Boolean>>>(completionJson)
                completionMap[date]?.get(goalId) ?: false
            } catch (e: Exception) {
                false
            }
        }
    }

    fun getResultsCompletionFlow(): Flow<String> {
        return context.resultsDataStore.data.map { preferences ->
            preferences[PreferencesKeys.RESULTS_COMPLETION] ?: "{}"
        }
    }

    suspend fun saveDailyNote(date: String, note: String) {
        context.resultsDataStore.edit { preferences ->
            val notesJson = preferences[PreferencesKeys.DAILY_NOTES] ?: "{}"
            val notesMap = try {
                json.decodeFromString<Map<String, String>>(notesJson).toMutableMap()
            } catch (e: Exception) {
                mutableMapOf()
            }
            
            notesMap[date] = note
            preferences[PreferencesKeys.DAILY_NOTES] = json.encodeToString(notesMap)
        }
    }

    fun getDailyNote(date: String): Flow<String> {
        return context.resultsDataStore.data.map { preferences ->
            val notesJson = preferences[PreferencesKeys.DAILY_NOTES] ?: "{}"
            try {
                val notesMap = json.decodeFromString<Map<String, String>>(notesJson)
                notesMap[date] ?: ""
            } catch (e: Exception) {
                ""
            }
        }
    }

    suspend fun clearAllData() {
        Log.d(TAG, "Début de clearAllData pour les résultats")
        context.resultsDataStore.edit { preferences ->
            preferences.clear()
        }
        // Forcer une mise à jour des résultats
        val currentLanguage = languageRepository.currentLanguage.first()
        _resultsFlow.value = when (currentLanguage) {
            Language.FRENCH -> defaultResultsFrench
            Language.ENGLISH -> defaultResultsEnglish
        }
        Log.d(TAG, "Réinitialisation des résultats terminée")
    }
} 