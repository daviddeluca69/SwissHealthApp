/**
 * ViewModel gérant la langue de l'application
 * 
 * Cette classe est responsable de :
 * - La gestion de la langue courante de l'application
 * - La persistance du choix de langue via le LanguageRepository
 * - La mise à disposition d'un StateFlow pour observer les changements de langue
 * - Le changement de langue à la demande de l'utilisateur
 */

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

    /**
     * Repository pour la gestion de la persistance de la langue
     */
    private val repository = LanguageRepository(application)

    /**
     * StateFlow exposant la langue courante
     * Utilise WhileSubscribed pour optimiser les ressources
     * Démarre avec le français comme valeur par défaut
     */
    val currentLanguage = repository.currentLanguage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Language.FRENCH
    )

    /**
     * Initialise le ViewModel et configure l'observation des changements de langue
     */
    init {
        Log.d(TAG, "Initialisation du LanguageViewModel")
        viewModelScope.launch {
            currentLanguage.collect { language ->
                Log.d(TAG, "Langue actuelle: $language")
            }
        }
    }

    /**
     * Change la langue de l'application
     * Cette action déclenche une mise à jour dans toute l'application
     * 
     * @param language Nouvelle langue à appliquer
     */
    fun setLanguage(language: Language) {
        Log.d(TAG, "Changement de langue demandé: $language")
        viewModelScope.launch {
            repository.setLanguage(language)
            Log.d(TAG, "Langue changée avec succès")
        }
    }
} 