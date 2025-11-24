package ru.tsvlad.root.helper.config

import kotlinx.serialization.Serializable

@Serializable
data class StepConfig(
    val picture: String
)

@Serializable
data class FractionConfig(
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val steps: List<StepConfig>
)

@Serializable
data class GameConfig(
    val fractions: List<FractionConfig>,
    val maxHistorySize: Int = 100 // Значение по умолчанию
)