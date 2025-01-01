package com.example.swisshealthapp.model

data class Settings(
    val dailyNotification: Boolean = false,
    val notificationTime: String = "20:00",
    val darkTheme: Boolean = false
) 