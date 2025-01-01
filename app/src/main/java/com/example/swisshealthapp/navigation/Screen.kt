package com.example.swisshealthapp.navigation

enum class Screen {
    DAILY_GOALS,
    STATS,
    SETTINGS,
    LANGUAGE;

    companion object {
        fun fromRoute(route: String?): Screen =
            when (route?.substringBefore("/")) {
                DAILY_GOALS.name -> DAILY_GOALS
                STATS.name -> STATS
                SETTINGS.name -> SETTINGS
                LANGUAGE.name -> LANGUAGE
                null -> DAILY_GOALS
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
} 