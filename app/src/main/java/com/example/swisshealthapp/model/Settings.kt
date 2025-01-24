package com.example.swisshealthapp.model

/**
 * Classe de données représentant les paramètres de l'application
 * 
 * Cette classe gère :
 * - Les préférences de notification quotidienne
 * - L'heure des notifications
 * - Le thème de l'application (clair/sombre)
 */

data class Settings(
    /**
     * Active ou désactive les notifications quotidiennes
     * Par défaut désactivé
     */
    val dailyNotification: Boolean = false,

    /**
     * Heure à laquelle envoyer la notification quotidienne
     * Format "HH:mm", par défaut à 20:00
     */
    val notificationTime: String = "20:00",

    /**
     * Préférence pour le thème sombre
     * Par défaut désactivé (thème clair)
     */
    val darkTheme: Boolean = false
) 