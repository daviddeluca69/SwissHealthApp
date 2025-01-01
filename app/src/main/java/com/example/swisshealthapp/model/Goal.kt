package com.example.swisshealthapp.model

data class Goal(
    val id: Int,
    val title: String,
    val points: Int,
    val details: String,
    var isCompleted: Boolean = false
) 