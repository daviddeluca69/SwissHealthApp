package com.example.swisshealthapp.model

object LocalizedStrings {
    fun get(key: String, language: Language): String {
        return when (language) {
            Language.FRENCH -> frenchStrings[key] ?: key
            Language.ENGLISH -> englishStrings[key] ?: key
        }
    }

    private val frenchStrings = mapOf(
        // Navigation
        "tab_goals" to "Objectifs",
        "tab_stats" to "Stats",
        "tab_settings" to "Param",
        "tab_results" to "Résultats",
        "tab_donation" to "Don",

        // Goals Screen
        "today" to "Aujourd'hui",
        "points" to "Points",
        "close" to "Fermer",
        "points_format" to "Points: %d / %d",
        "goal_details" to "Détails de l'objectif",

        // Stats Screen
        "last_ten_days_points" to "Points des 10 derniers jours",
        "today_stats" to "Aujourd'hui",
        "average" to "Moyenne",
        "maximum" to "Maximum",
        "goals_stats" to "Objectifs",
        "results_stats" to "Résultats",
        "pts" to "pts",

        // Settings Screen
        "settings" to "Paramètres",
        "goals_management" to "Gestion des objectifs",
        "goals_management_info" to "Pour une meilleure lisibilité, si vous supprimez/ajoutez ou modifiez un objectif, vérifiez d'avoir toujours un nombre total de points égal à 100",
        "language_section" to "Langue",
        "add_goal" to "Ajouter un objectif",
        "edit_goal" to "Modifier",
        "delete_goal" to "Supprimer",
        "reset_section" to "Réinitialisation",
        "reset_warning" to "Attention : cette action effacera toutes les données de l'application et restaurera les objectifs par défaut.",
        "reset_app" to "Réinitialiser l'application",
        "delete_goal_confirmation" to "Êtes-vous sûr de vouloir supprimer l'objectif \"%s\" ?",
        "reset_confirmation" to "Êtes-vous sûr de vouloir réinitialiser l'application ? Cette action effacera toutes vos données et ne pourra pas être annulée.",
        "cancel" to "Annuler",
        "confirm_delete" to "Supprimer",
        "confirm_reset" to "Réinitialiser",
        "new_goal" to "Nouvel objectif",
        "edit_goal_title" to "Modifier l'objectif",
        "goal_title_label" to "Titre de l'objectif",
        "goal_points_label" to "Points",
        "goal_details_label" to "Détails",
        "save" to "Enregistrer",

        // Results Screen
        "results_title" to "Résultats",
        "results_coming_soon" to "Cette fonctionnalité sera bientôt disponible !",

        // Donation Screen
        "donation_title" to "Soutenir le développeur",
        "donation_message" to "Si cette application vous est utile et vous aide à maintenir de bonnes habitudes de santé, vous pouvez soutenir son développement. Votre contribution permettra d'améliorer l'application et d'ajouter de nouvelles fonctionnalités.",
        "donation_email" to "Contact par email :",
        "donation_bitcoin" to "Don par Bitcoin :",
        "copy_to_clipboard" to "Copier",
        "copied_to_clipboard" to "Copié !",
        "make_donation" to "Faire un don au développeur"
    )

    private val englishStrings = mapOf(
        // Navigation
        "tab_goals" to "Goals",
        "tab_stats" to "Statistics",
        "tab_settings" to "Settings",
        "tab_results" to "Results",
        "tab_donation" to "Donate",

        // Goals Screen
        "today" to "Today",
        "points" to "Points",
        "close" to "Close",
        "points_format" to "Points: %d / %d",
        "goal_details" to "Goal Details",

        // Stats Screen
        "last_ten_days_points" to "Points for the last 10 days",
        "today_stats" to "Today",
        "average" to "Average",
        "maximum" to "Maximum",
        "goals_stats" to "Goals",
        "results_stats" to "Results",
        "pts" to "pts",

        // Settings Screen
        "settings" to "Settings",
        "goals_management" to "Goals Management",
        "goals_management_info" to "For better readability, if you delete/add or modify a goal, make sure to always have a total of 100 points",
        "language_section" to "Language",
        "add_goal" to "Add Goal",
        "edit_goal" to "Edit",
        "delete_goal" to "Delete",
        "reset_section" to "Reset",
        "reset_warning" to "Warning: this action will erase all application data and restore default goals.",
        "reset_app" to "Reset Application",
        "delete_goal_confirmation" to "Are you sure you want to delete the goal \"%s\"?",
        "reset_confirmation" to "Are you sure you want to reset the application? This action will erase all your data and cannot be undone.",
        "cancel" to "Cancel",
        "confirm_delete" to "Delete",
        "confirm_reset" to "Reset",
        "new_goal" to "New Goal",
        "edit_goal_title" to "Edit Goal",
        "goal_title_label" to "Goal Title",
        "goal_points_label" to "Points",
        "goal_details_label" to "Details",
        "save" to "Save",

        // Results Screen
        "results_title" to "Results",
        "results_coming_soon" to "This feature will be available soon!",

        // Donation Screen
        "donation_title" to "Support the Developer",
        "donation_message" to "If this app is helpful and helps you maintain good health habits, you can support its development. Your contribution will help improve the app and add new features.",
        "donation_email" to "Contact by email:",
        "donation_bitcoin" to "Donate with Bitcoin:",
        "copy_to_clipboard" to "Copy",
        "copied_to_clipboard" to "Copied!",
        "make_donation" to "Support the Developer"
    )
} 