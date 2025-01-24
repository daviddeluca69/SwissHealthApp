package com.example.swisshealthapp.model

enum class Language(val code: String, val displayName: String) {
    FRENCH("fr", "Français"),

    /**
     * Langue anglaise, alternative disponible
     */
    ENGLISH("en", "English");

    companion object {
        /**
         * Convertit un code de langue en objet Language
         * Retourne le français par défaut si le code n'est pas reconnu
         * 
         * @param code Code ISO de la langue à convertir
         * @return Objet Language correspondant ou FRENCH par défaut
         */
        fun fromCode(code: String): Language {
            return values().find { it.code == code } ?: FRENCH
        }
    }
} 