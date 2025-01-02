package com.example.swisshealthapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.swisshealthapp.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_settings")

class LanguageRepository(private val context: Context) {
    private object PreferencesKeys {
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
    }

    val currentLanguage: Flow<Language> = context.languageDataStore.data
        .map { preferences ->
            val languageCode = preferences[PreferencesKeys.LANGUAGE_CODE] ?: Language.FRENCH.code
            Language.fromCode(languageCode)
        }

    suspend fun setLanguage(language: Language) {
        context.languageDataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE_CODE] = language.code
        }
    }
} 