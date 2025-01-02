package com.example.swisshealthapp.model

enum class Language(val code: String, val displayName: String) {
    FRENCH("fr", "Français"),
    ENGLISH("en", "English");

    companion object {
        fun fromCode(code: String): Language {
            return values().find { it.code == code } ?: FRENCH
        }
    }
} 