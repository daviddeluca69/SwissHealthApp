/**
 * Repository gérant la persistance de la langue de l'application
 * 
 * Cette classe est responsable de :
 * - La sauvegarde de la langue sélectionnée via DataStore
 * - La récupération de la langue courante
 * - La conversion entre codes de langue et objets Language
 * 
 * Elle utilise DataStore pour une persistance efficace et réactive des préférences
 */

package com.example.swisshealthapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.swisshealthapp.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Extension property pour accéder au DataStore des préférences de langue
 * Utilise un nom unique pour éviter les conflits
 */
private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_settings")

class LanguageRepository(private val context: Context) {
    /**
     * Clés utilisées pour stocker les préférences dans DataStore
     */
    private object PreferencesKeys {
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
    }

    /**
     * Flow exposant la langue courante de l'application
     * Se met à jour automatiquement lors des changements dans DataStore
     * Utilise le français comme langue par défaut
     */
    val currentLanguage: Flow<Language> = context.languageDataStore.data
        .map { preferences ->
            val languageCode = preferences[PreferencesKeys.LANGUAGE_CODE] ?: Language.FRENCH.code
            Language.fromCode(languageCode)
        }

    /**
     * Change la langue de l'application
     * Sauvegarde le nouveau choix dans DataStore
     * 
     * @param language Nouvelle langue à appliquer
     */
    suspend fun setLanguage(language: Language) {
        context.languageDataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE_CODE] = language.code
        }
    }
} 