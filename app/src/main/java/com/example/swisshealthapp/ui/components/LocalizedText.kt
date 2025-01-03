package com.example.swisshealthapp.ui.components

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

@Composable
fun LocalizedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    viewModel: LanguageViewModel = viewModel()
) {
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    Text(
        text = LocalizedStrings.get(text, currentLanguage),
        modifier = modifier,
        style = style
    )
}

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