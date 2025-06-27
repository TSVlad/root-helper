package ru.tsvlad.roothelper.config

import kotlinx.serialization.Serializable

@Serializable
data class StepConfig(
    val description: String
)

@Serializable
data class StageConfig(
    val name: String,
    val icon: String,
    val steps: List<StepConfig>
)

@Serializable
data class FractionConfig(
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val stages: List<StageConfig>
)

@Serializable
data class GameConfig(
    val fractions: List<FractionConfig>,
    val maxHistorySize: Int = 100 // Значение по умолчанию
)