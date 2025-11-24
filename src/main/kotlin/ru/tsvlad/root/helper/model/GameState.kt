package ru.tsvlad.root.helper.model

data class GameState(
    val factionIndex: Int,
    val stepIndex: Int,
    val completedTurns: Int
)