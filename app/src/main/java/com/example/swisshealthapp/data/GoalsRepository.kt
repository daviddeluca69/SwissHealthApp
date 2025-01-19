package com.example.swisshealthapp.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.swisshealthapp.model.Goal
import com.example.swisshealthapp.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

private val Context.goalsDataStore: DataStore<Preferences> by preferencesDataStore(name = "goals")

class GoalsRepository(private val context: Context) {
    private val TAG = "GoalsRepository"
    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true
    }
    private val languageRepository = LanguageRepository(context)
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private object PreferencesKeys {
        val GOALS = stringPreferencesKey("goals")
        val GOALS_COMPLETION = stringPreferencesKey("goals_completion")
        val INITIALIZED = booleanPreferencesKey("initialized")
    }

    private val defaultGoalsFrench = listOf(
        Goal(
            id = 1,
            title = "Faire 30 minutes d'exercice",
            points = 10,
            details = "Faire au moins 30 minutes d'activité physique modérée à intense"
        ),
        Goal(
            id = 2,
            title = "Manger 5 fruits et légumes",
            points = 10,
            details = "Consommer au moins 5 portions de fruits et légumes dans la journée"
        ),
        Goal(
            id = 3,
            title = "Boire 2L d'eau",
            points = 10,
            details = "Boire au moins 2 litres d'eau tout au long de la journée"
        ),
        Goal(
            id = 4,
            title = "Dormir 8 heures",
            points = 10,
            details = "Avoir une nuit de sommeil d'au moins 8 heures"
        ),
        Goal(
            id = 5,
            title = "Méditer 10 minutes",
            points = 10,
            details = "Pratiquer la méditation ou la relaxation pendant 10 minutes"
        ),
        Goal(
            id = 6,
            title = "Manger équilibré",
            points = 10,
            details = "Prendre 3 repas équilibrés dans la journée"
        ),
        Goal(
            id = 7,
            title = "Limiter les écrans",
            points = 10,
            details = "Limiter l'utilisation des écrans à 2 heures de loisirs par jour"
        ),
        Goal(
            id = 8,
            title = "Activité sociale",
            points = 10,
            details = "Avoir au moins une interaction sociale positive"
        ),
        Goal(
            id = 9,
            title = "Prendre l'air",
            points = 10,
            details = "Passer au moins 30 minutes en extérieur"
        ),
        Goal(
            id = 10,
            title = "Hygiène dentaire",
            points = 10,
            details = "Se brosser les dents au moins deux fois dans la journée"
        )
    )

    private val defaultGoalsEnglish = listOf(
        Goal(
            id = 1,
            title = "Exercise for 30 minutes",
            points = 10,
            details = "Do at least 30 minutes of moderate to intense physical activity"
        ),
        Goal(
            id = 2,
            title = "Eat 5 fruits and vegetables",
            points = 10,
            details = "Consume at least 5 servings of fruits and vegetables during the day"
        ),
        Goal(
            id = 3,
            title = "Drink 2L of water",
            points = 10,
            details = "Drink at least 2 liters of water throughout the day"
        ),
        Goal(
            id = 4,
            title = "Sleep 8 hours",
            points = 10,
            details = "Get at least 8 hours of sleep"
        ),
        Goal(
            id = 5,
            title = "Meditate for 10 minutes",
            points = 10,
            details = "Practice meditation or relaxation for 10 minutes"
        ),
        Goal(
            id = 6,
            title = "Eat balanced meals",
            points = 10,
            details = "Have 3 balanced meals during the day"
        ),
        Goal(
            id = 7,
            title = "Limit screen time",
            points = 10,
            details = "Limit leisure screen time to 2 hours per day"
        ),
        Goal(
            id = 8,
            title = "Social activity",
            points = 10,
            details = "Have at least one positive social interaction"
        ),
        Goal(
            id = 9,
            title = "Get fresh air",
            points = 10,
            details = "Spend at least 30 minutes outdoors"
        ),
        Goal(
            id = 10,
            title = "Dental hygiene",
            points = 10,
            details = "Brush teeth at least twice a day"
        )
    )

    val goals: Flow<List<Goal>> = context.goalsDataStore.data
        .map { preferences ->
            val goalsJson = preferences[PreferencesKeys.GOALS] ?: "[]"
            try {
                json.decodeFromString<List<Goal>>(goalsJson)
            } catch (e: Exception) {
                emptyList()
            }
        }

    init {
        Log.d(TAG, "Initialisation du GoalsRepository")
        scope.launch {
            languageRepository.currentLanguage.collect { language ->
                Log.d(TAG, "Changement de langue détecté: $language")
                updateGoalsForLanguage(language)
            }
        }
    }

    fun onCleared() {
        scope.cancel()
    }

    private suspend fun updateGoalsForLanguage(language: Language) {
        Log.d(TAG, "Mise à jour des objectifs pour la langue: $language")
        val currentGoals = goals.first()
        if (currentGoals.isEmpty()) {
            Log.d(TAG, "Pas d'objectifs existants, initialisation...")
            initializeIfNeeded()
            return
        }

        val defaultGoals = when (language) {
            Language.FRENCH -> defaultGoalsFrench
            Language.ENGLISH -> defaultGoalsEnglish
        }

        // Mettre à jour les objectifs existants avec les nouveaux titres et détails
        val updatedGoals = currentGoals.map { currentGoal ->
            val defaultGoal = defaultGoals.find { it.id == currentGoal.id }
            if (defaultGoal != null) {
                currentGoal.copy(
                    title = defaultGoal.title,
                    details = defaultGoal.details
                )
            } else {
                currentGoal
            }
        }
        Log.d(TAG, "Sauvegarde des objectifs mis à jour: ${updatedGoals.size}")
        saveGoals(updatedGoals)
    }

    suspend fun initializeIfNeeded() {
        Log.d(TAG, "Début de initializeIfNeeded")
        val preferences = context.goalsDataStore.data.first()
        val isInitialized = preferences[PreferencesKeys.INITIALIZED].isTrue()
        Log.d(TAG, "État d'initialisation: $isInitialized")
        
        if (!isInitialized) {
            val currentLanguage = languageRepository.currentLanguage.first()
            Log.d(TAG, "Langue actuelle: $currentLanguage")
            
            val defaultGoals = when (currentLanguage) {
                Language.FRENCH -> {
                    Log.d(TAG, "Utilisation des objectifs français")
                    defaultGoalsFrench
                }
                Language.ENGLISH -> {
                    Log.d(TAG, "Utilisation des objectifs anglais")
                    defaultGoalsEnglish
                }
            }
            Log.d(TAG, "Sauvegarde de ${defaultGoals.size} objectifs")
            saveGoals(defaultGoals)
            context.goalsDataStore.edit { prefs ->
                prefs[PreferencesKeys.INITIALIZED] = true
            }
            Log.d(TAG, "Initialisation terminée")
        } else {
            Log.d(TAG, "Déjà initialisé, pas de changement")
        }
    }

    private fun Boolean?.isTrue() = this == true

    suspend fun saveGoals(goals: List<Goal>) {
        Log.d(TAG, "Sauvegarde de ${goals.size} objectifs")
        Log.d(TAG, "Premier objectif: ${goals.firstOrNull()?.title}")
        context.goalsDataStore.edit { preferences ->
            val goalsJson = json.encodeToString(goals)
            preferences[PreferencesKeys.GOALS] = goalsJson
        }
    }

    suspend fun updateGoalCompletion(goalId: Int, date: String, isCompleted: Boolean) {
        context.goalsDataStore.edit { preferences ->
            val completionJson = preferences[PreferencesKeys.GOALS_COMPLETION] ?: "{}"
            val completionMap = try {
                json.decodeFromString<Map<String, Map<Int, Boolean>>>(completionJson).toMutableMap()
            } catch (e: Exception) {
                mutableMapOf()
            }
            
            val dateMap = completionMap.getOrDefault(date, mutableMapOf()).toMutableMap()
            dateMap[goalId] = isCompleted
            completionMap[date] = dateMap
            
            preferences[PreferencesKeys.GOALS_COMPLETION] = json.encodeToString(completionMap)
        }
    }

    fun getGoalCompletionStatus(goalId: Int, date: String): Flow<Boolean> {
        return context.goalsDataStore.data.map { preferences ->
            val completionJson = preferences[PreferencesKeys.GOALS_COMPLETION] ?: "{}"
            try {
                val completionMap = json.decodeFromString<Map<String, Map<Int, Boolean>>>(completionJson)
                completionMap[date]?.get(goalId) ?: false
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun clearAllData() {
        Log.d(TAG, "Début de clearAllData")
        context.goalsDataStore.edit { preferences ->
            preferences.clear()
        }
        val currentLanguage = languageRepository.currentLanguage.first()
        Log.d(TAG, "Langue actuelle pour réinitialisation: $currentLanguage")
        
        val defaultGoals = when (currentLanguage) {
            Language.FRENCH -> {
                Log.d(TAG, "Réinitialisation avec objectifs français")
                defaultGoalsFrench
            }
            Language.ENGLISH -> {
                Log.d(TAG, "Réinitialisation avec objectifs anglais")
                defaultGoalsEnglish
            }
        }
        saveGoals(defaultGoals)
        context.goalsDataStore.edit { prefs ->
            prefs[PreferencesKeys.INITIALIZED] = true
        }
        Log.d(TAG, "Réinitialisation terminée")
    }
} 