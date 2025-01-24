/**
 * Repository gérant les paramètres des objectifs de santé
 * 
 * Cette classe est responsable de :
 * - La persistance des paramètres des objectifs via DataStore
 * - La gestion de la liste des objectifs personnalisés
 * - La sérialisation et désérialisation des objectifs en JSON
 * 
 * Elle utilise DataStore pour une persistance efficace et réactive des données
 */

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

/**
 * Extension property pour accéder au DataStore des paramètres des objectifs
 * Utilise un nom unique pour éviter les conflits
 */
private val Context.goalsDataStore: DataStore<Preferences> by preferencesDataStore(name = "goals_settings")

class GoalsSettingsRepository(private val context: Context) {
    /**
     * Configuration du sérialiseur JSON pour la persistance
     * Ignore les champs inconnus et utilise un format lisible
     */
    private val json = Json { 
        ignoreUnknownKeys = true 
        prettyPrint = true
    }

    /**
     * Clés utilisées pour stocker les préférences dans DataStore
     */
    private object PreferencesKeys {
        val GOALS = stringPreferencesKey("goals")
    }

    /**
     * Flow exposant la liste des objectifs personnalisés
     * Se met à jour automatiquement lors des modifications
     * Retourne une liste vide en cas d'erreur de désérialisation
     */
    val goals: Flow<List<Goal>> = context.goalsDataStore.data
        .map { preferences ->
            val goalsJson = preferences[PreferencesKeys.GOALS] ?: "[]"
            try {
                json.decodeFromString<List<Goal>>(goalsJson)
            } catch (e: Exception) {
                emptyList()
            }
        }

    /**
     * Sauvegarde une nouvelle liste d'objectifs personnalisés
     * 
     * @param goals Liste des objectifs à sauvegarder
     */
    suspend fun saveGoals(goals: List<Goal>) {
        context.goalsDataStore.edit { preferences ->
            val goalsJson = json.encodeToString(goals)
            preferences[PreferencesKeys.GOALS] = goalsJson
        }
    }
} 