package com.example.swisshealthapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.swisshealthapp.model.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

private val Context.goalsDataStore: DataStore<Preferences> by preferencesDataStore(name = "goals_settings")

class GoalsSettingsRepository(private val context: Context) {
    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true
    }

    private object PreferencesKeys {
        val GOALS = stringPreferencesKey("goals")
    }

    val goals: Flow<List<Goal>> = context.goalsDataStore.data
        .map { preferences ->
            val goalsJson = preferences[PreferencesKeys.GOALS] ?: "[]"
            try {
                json.decodeFromString<List<Goal>>(goalsJson)
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun saveGoals(goals: List<Goal>) {
        context.goalsDataStore.edit { preferences ->
            val goalsJson = json.encodeToString(goals)
            preferences[PreferencesKeys.GOALS] = goalsJson
        }
    }
} 