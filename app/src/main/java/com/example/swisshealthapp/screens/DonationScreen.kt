/**
 * Écran de don de l'application Swiss Health
 * 
 * Cet écran permet aux utilisateurs de soutenir le développement de l'application en :
 * - Affichant un message explicatif sur l'importance des dons
 * - Proposant deux moyens de contribution :
 *   1. Contact par email pour les questions ou dons traditionnels
 *   2. Don direct par Bitcoin pour les paiements cryptographiques
 * 
 * L'interface propose des boutons de copie rapide pour faciliter l'utilisation
 * des adresses email et Bitcoin, avec des confirmations visuelles temporaires
 */

package com.example.swisshealthapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.swisshealthapp.ui.components.LocalizedText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Composant principal de l'écran de don
 * Gère l'affichage des informations de contact et l'interaction avec le presse-papiers
 */
@Composable
fun DonationScreen() {
    // Gestionnaire du presse-papiers pour la copie des adresses
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    
    // États pour gérer les messages de confirmation de copie
    var showEmailCopiedMessage by remember { mutableStateOf(false) }
    var showBitcoinCopiedMessage by remember { mutableStateOf(false) }

    // Informations de contact et de paiement
    val email = "daviddeluca69@gmail.com"
    val bitcoinAddress = "bc1qr83qlc50k06e5vtka5m30e5urrylc3s765zzl8"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Titre de la section
        LocalizedText(
            text = "donation_title",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Carte principale contenant les informations
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Message explicatif
                LocalizedText(
                    text = "donation_message",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section Email
                LocalizedText(
                    text = "donation_email",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(email))
                            showEmailCopiedMessage = true
                            scope.launch {
                                delay(2000)
                                showEmailCopiedMessage = false
                            }
                        }
                    ) {
                        LocalizedText(
                            text = if (showEmailCopiedMessage) "copied_to_clipboard" else "copy_to_clipboard"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section Bitcoin
                LocalizedText(
                    text = "donation_bitcoin",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = bitcoinAddress,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(bitcoinAddress))
                            showBitcoinCopiedMessage = true
                            scope.launch {
                                delay(2000)
                                showBitcoinCopiedMessage = false
                            }
                        }
                    ) {
                        LocalizedText(
                            text = if (showBitcoinCopiedMessage) "copied_to_clipboard" else "copy_to_clipboard"
                        )
                    }
                }
            }
        }
    }
} 