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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.swisshealthapp.ui.components.LocalizedText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.swisshealthapp.model.LocalizedStrings
import com.example.swisshealthapp.model.Language

/**
 * Composant principal de l'écran de don
 * Gère l'affichage des informations de contact et l'interaction avec le presse-papiers
 */
@Composable
fun DonationScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    // Informations de contact et de paiement
    val email = "daviddeluca69@gmail.com"
    val bitcoinAddress = "bc1qr83qlc50k06e5vtka5m30e5urrylc3s765zzl8"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Section Premium
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LocalizedText(
                    text = "premium_title",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                LocalizedText(
                    text = "premium_message",
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("market://details?id=com.example.swisshealthpremiumapp")
                            setPackage("com.android.vending")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LocalizedText(text = "premium_cta")
                }
            }
        }

        // Section Support Developer
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LocalizedText(
                    text = "donation_title",
                    style = MaterialTheme.typography.headlineSmall
                )

                LocalizedText(
                    text = "donation_message",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Email Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LocalizedText(
                        text = "donation_email",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                copyToClipboard(context, email)
                                showToast(context)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LocalizedText(text = "copy_to_clipboard")
                        }
                    }
                }

                // Bitcoin Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LocalizedText(
                        text = "donation_bitcoin",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = bitcoinAddress,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                copyToClipboard(context, bitcoinAddress)
                                showToast(context)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LocalizedText(text = "copy_to_clipboard")
                        }
                    }
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("text", text)
    clipboard.setPrimaryClip(clip)
}

private fun showToast(context: Context) {
    val message = LocalizedStrings.get("copied_to_clipboard", Language.FRENCH)
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
} 