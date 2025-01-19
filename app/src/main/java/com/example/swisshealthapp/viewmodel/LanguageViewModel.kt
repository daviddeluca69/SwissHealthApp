package com.example.swisshealthapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.swisshealthapp.data.LanguageRepository
import com.example.swisshealthapp.model.Language
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LanguageViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "LanguageViewModel"
    private val repository = LanguageRepository(application)
    val currentLanguage = repository.currentLanguage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Language.FRENCH
    )

    init {
        Log.d(TAG, "Initialisation du LanguageViewModel")
        viewModelScope.launch {
            currentLanguage.collect { language ->
                Log.d(TAG, "Langue actuelle: $language")
            }
        }
    }

    fun setLanguage(language: Language) {
        Log.d(TAG, "Changement de langue demandé: $language")
        viewModelScope.launch {
            repository.setLanguage(language)
            Log.d(TAG, "Langue changée avec succès")
        }
    }
} 