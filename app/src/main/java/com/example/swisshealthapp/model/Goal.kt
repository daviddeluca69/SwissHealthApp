package com.example.swisshealthapp.model

/**
 * Classe de données représentant un objectif ou un résultat de santé
 * 
 * Cette classe est utilisée pour :
 * - Définir les objectifs quotidiens de l'utilisateur
 * - Représenter les résultats de santé à suivre
 * - Stocker l'état de complétion
 * 
 * La classe est sérialisable pour permettre la persistance des données
 */

import kotlinx.serialization.Serializable

@Serializable
data class Goal(
    /**
     * Identifiant unique de l'objectif
     */
    val id: Int,

    /**
     * Titre descriptif de l'objectif
     */
    val title: String,

    /**
     * Nombre de points attribués à l'objectif
     * La somme des points de tous les objectifs devrait être égale à 100
     */
    val points: Int,

    /**
     * Description détaillée de l'objectif
     * Fournit des informations supplémentaires sur la manière d'atteindre l'objectif
     */
    val details: String,

    /**
     * État de complétion de l'objectif
     * Par défaut à false, mis à jour lorsque l'utilisateur marque l'objectif comme complété
     */
    val isCompleted: Boolean = false
) 