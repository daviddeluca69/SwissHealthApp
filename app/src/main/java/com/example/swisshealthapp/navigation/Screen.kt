package com.example.swisshealthapp.navigation

enum class Screen {
    DAILY_GOALS,
    RESULTS,
    STATS,
    SETTINGS,
    DONATION;

    companion object {
        fun fromRoute(route: String?): Screen =
            when (route?.substringBefore("/")) {
                DAILY_GOALS.name -> DAILY_GOALS
                RESULTS.name -> RESULTS
                STATS.name -> STATS
                SETTINGS.name -> SETTINGS
                DONATION.name -> DONATION
                null -> DAILY_GOALS
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
} 