package ru.tsvlad.roothelper.model

data class GameState(
    val factionIndex: Int,
    val stageIndex: Int,
    val stepIndex: Int,
    val completedTurns: Int
)