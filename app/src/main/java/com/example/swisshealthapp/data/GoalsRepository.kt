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
            points = 8,
            details = "Consommer au moins 5 portions de fruits et légumes dans la journée"
        ),
        Goal(
            id = 3,
            title = "Boire 2L d'eau",
            points = 5,
            details = "Boire au moins 2 litres d'eau tout au long de la journée"
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
} 