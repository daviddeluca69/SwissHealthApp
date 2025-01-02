package com.example.swisshealthapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.swisshealthapp.data.LanguageRepository
import com.example.swisshealthapp.model.Language
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LanguageViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LanguageRepository(application)
    val currentLanguage = repository.currentLanguage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Language.FRENCH
    )

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            repository.setLanguage(language)
        }
    }
} 