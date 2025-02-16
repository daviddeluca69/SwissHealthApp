package com.example.swisshealthapp.ui.components

/**
 * Composants Compose pour l'affichage de texte localisé
 * 
 * Ce fichier fournit deux composants :
 * - LocalizedText : pour afficher un texte simple localisé
 * - LocalizedTextWithParams : pour afficher un texte localisé avec des paramètres
 * 
 * Les composants s'adaptent automatiquement à la langue sélectionnée
 */

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swisshealthapp.model.LocalizedStrings
import com.example.swisshealthapp.viewmodel.LanguageViewModel
import androidx.compose.ui.graphics.Color

/**
 * Composant pour afficher un texte localisé simple
 * 
 * @param text Clé de la chaîne à localiser
 * @param modifier Modificateur Compose optionnel
 * @param style Style de texte à appliquer
 * @param color Couleur du texte
 * @param viewModel ViewModel gérant la langue courante
 */
@Composable
fun LocalizedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    viewModel: LanguageViewModel = viewModel()
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    Text(
        text = LocalizedStrings.get(text, currentLanguage),
        modifier = modifier,
        style = style,
        color = color
    )
}

/**
 * Composant pour afficher un texte localisé avec des paramètres
 * Utilise String.format pour insérer les paramètres dans le texte
 * 
 * @param text Clé de la chaîne à localiser
 * @param params Paramètres à insérer dans le texte
 * @param modifier Modificateur Compose optionnel
 * @param style Style de texte à appliquer
 * @param viewModel ViewModel gérant la langue courante
 */
@Composable
fun LocalizedTextWithParams(
    text: String,
    vararg params: Any,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    viewModel: LanguageViewModel = viewModel()
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    Text(
        text = String.format(LocalizedStrings.get(text, currentLanguage), *params),
        modifier = modifier,
        style = style
    )
} 