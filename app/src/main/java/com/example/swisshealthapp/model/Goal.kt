package com.example.swisshealthapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Goal(
    val id: Int,
    val title: String,
    val points: Int,
    val details: String,
    val isCompleted: Boolean = false
) 