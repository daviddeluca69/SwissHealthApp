package com.example.swisshealthapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.swisshealthapp.model.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "goals")

class GoalsRepository(private val context: Context) {
    
    fun getGoalCompletionStatus(goalId: Int): Flow<Boolean> {
        val key = booleanPreferencesKey("goal_$goalId")
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: false
        }
    }

    suspend fun saveGoalCompletionStatus(goalId: Int, isCompleted: Boolean) {
        val key = booleanPreferencesKey("goal_$goalId")
        context.dataStore.edit { preferences ->
            preferences[key] = isCompleted
        }
    }
} 