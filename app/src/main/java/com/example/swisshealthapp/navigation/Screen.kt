/**
 * Énumération des écrans de l'application Swiss Health
 * 
 * Cette énumération définit tous les écrans disponibles dans l'application :
 * - DAILY_GOALS : Écran principal des objectifs quotidiens
 * - RESULTS : Écran de suivi des résultats journaliers
 * - STATS : Écran des statistiques et tendances
 * - SETTINGS : Écran de configuration de l'application
 * - DONATION : Écran de support au développement
 * 
 * Utilisée par la navigation pour gérer les transitions entre écrans
 * et par la barre de navigation inférieure pour l'affichage des onglets
 */
package com.example.swisshealthapp.navigation

enum class Screen {
    /**
     * Écran principal affichant les objectifs de santé du jour
     * Permet de marquer les objectifs comme complétés
     */
    DAILY_GOALS,

    /**
     * Écran de suivi des résultats quotidiens
     * Permet d'ajouter des notes et de voir l'historique
     */
    RESULTS,

    /**
     * Écran des statistiques montrant les tendances
     * Affiche un graphique comparatif sur 10 jours
     */
    STATS,

    /**
     * Écran des paramètres pour personnaliser l'application
     * Gestion des objectifs et de la langue
     */
    SETTINGS,

    /**
     * Écran permettant de soutenir le développement
     * Options de don par email ou Bitcoin
     */
    DONATION;
} 