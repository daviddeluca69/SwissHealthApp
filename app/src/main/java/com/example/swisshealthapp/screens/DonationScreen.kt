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

@Composable
fun DonationScreen() {
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var showEmailCopiedMessage by remember { mutableStateOf(false) }
    var showBitcoinCopiedMessage by remember { mutableStateOf(false) }

    val email = "daviddeluca69@gmail.com"
    val bitcoinAddress = "bc1qr83qlc50k06e5vtka5m30e5urrylc3s765zzl8"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LocalizedText(
            text = "donation_title",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                LocalizedText(
                    text = "donation_message",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Email Section
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

                // Bitcoin Section
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