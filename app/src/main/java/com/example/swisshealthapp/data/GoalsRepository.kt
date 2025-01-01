package com.example.swisshealthapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.swisshealthapp.model.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "goals")

class GoalsRepository(private val context: Context) {
    
    private fun getGoalKey(goalId: Int, date: LocalDate): String {
        val dateStr = date.format(DateTimeFormatter.ISO_DATE)
        return "goal_${goalId}_$dateStr"
    }
    
    fun getGoalCompletionStatus(goalId: Int, date: LocalDate): Flow<Boolean> {
        val key = booleanPreferencesKey(getGoalKey(goalId, date))
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: false
        }
    }

    suspend fun saveGoalCompletionStatus(goalId: Int, date: LocalDate, isCompleted: Boolean) {
        val key = booleanPreferencesKey(getGoalKey(goalId, date))
        context.dataStore.edit { preferences ->
            preferences[key] = isCompleted
        }
    }

    suspend fun getDailyPoints(date: LocalDate, goals: List<Goal>): Int {
        var totalPoints = 0
        for (goal in goals) {
            val isCompleted = getGoalCompletionStatus(goal.id, date).first()
            if (isCompleted) {
                totalPoints += goal.points
            }
        }
        return totalPoints
    }
} 