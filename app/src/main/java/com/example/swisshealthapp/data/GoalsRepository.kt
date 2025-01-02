package com.example.swisshealthapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.swisshealthapp.model.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

private val Context.goalsDataStore: DataStore<Preferences> by preferencesDataStore(name = "goals")

class GoalsRepository(private val context: Context) {
    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true
    }

    private object PreferencesKeys {
        val GOALS = stringPreferencesKey("goals")
        val GOALS_COMPLETION = stringPreferencesKey("goals_completion")
        val INITIALIZED = booleanPreferencesKey("initialized")
    }

    private val defaultGoals = listOf(
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

    val goals: Flow<List<Goal>> = context.goalsDataStore.data
        .map { preferences ->
            val goalsJson = preferences[PreferencesKeys.GOALS] ?: "[]"
            try {
                json.decodeFromString<List<Goal>>(goalsJson)
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun initializeIfNeeded() {
        val preferences = context.goalsDataStore.data.first()
        if (!preferences[PreferencesKeys.INITIALIZED].isTrue()) {
            saveGoals(defaultGoals)
            context.goalsDataStore.edit { prefs ->
                prefs[PreferencesKeys.INITIALIZED] = true
            }
        }
    }

    private fun Boolean?.isTrue() = this == true

    suspend fun saveGoals(goals: List<Goal>) {
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
        context.goalsDataStore.edit { preferences ->
            preferences.clear()
        }
        initializeIfNeeded() // Réinitialiser avec les objectifs par défaut
    }
} 