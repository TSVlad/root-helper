package ru.tsvlad.roothelper.controller

import ru.tsvlad.roothelper.config.FractionConfig
import ru.tsvlad.roothelper.config.GameConfig
import ru.tsvlad.roothelper.config.StageConfig
import ru.tsvlad.roothelper.config.StepConfig

class GameController(private val config: GameConfig) {
    private val factions = config.fractions
    private var currentFactionIndex = 0
    private var currentStageIndex = 0
    private var currentStepIndex = 0
    private var completedTurns = 0
    private val history = mutableListOf<GameState>()
    private var historyPointer = 0 // Указатель на текущее состояние в истории

    init {
        // Сохраняем начальное состояние
        saveState()
    }

    fun currentState(): Triple<FractionConfig, StageConfig, StepConfig> {
        val state = history[historyPointer]
        val fraction = factions[state.factionIndex]
        val stage = fraction.stages[state.stageIndex]
        val step = stage.steps[state.stepIndex]
        return Triple(fraction, stage, step)
    }

    fun nextStep() {
        // Если мы не в конце истории, просто переходим вперед
        if (historyPointer < history.size - 1) {
            historyPointer++
            return
        }

        // Создаем новое состояние
        val currentState = history[historyPointer]
        var newFactionIndex = currentState.factionIndex
        var newStageIndex = currentState.stageIndex
        var newStepIndex = currentState.stepIndex
        var newTurns = currentState.completedTurns

        val faction = factions[newFactionIndex]
        val stage = faction.stages[newStageIndex]

        if (newStepIndex < stage.steps.size - 1) {
            newStepIndex++
        } else {
            newStepIndex = 0
            if (newStageIndex < faction.stages.size - 1) {
                newStageIndex++
            } else {
                newStageIndex = 0
                newFactionIndex = (newFactionIndex + 1) % factions.size
                if (newFactionIndex == 0) {
                    newTurns++
                }
            }
        }

        // Сохраняем новое состояние
        saveState(GameState(newFactionIndex, newStageIndex, newStepIndex, newTurns))
        historyPointer = history.size - 1
    }

    fun prevStep() {
        if (historyPointer > 0) {
            historyPointer--
        }
    }

    fun getCurrentTurn(): Int {
        return history[historyPointer].completedTurns + 1
    }

    private fun saveState(state: GameState? = null) {
        val currentState = state ?: GameState(
            currentFactionIndex,
            currentStageIndex,
            currentStepIndex,
            completedTurns
        )

        // Если мы не в конце истории, удаляем все состояния после текущего указателя
        if (historyPointer < history.size - 1) {
            history.subList(historyPointer + 1, history.size).clear()
        }

        // Добавляем новое состояние
        history.add(currentState)

        // Ограничиваем размер истории
        if (history.size > config.maxHistorySize) {
            val toRemove = history.size - config.maxHistorySize
            history.subList(0, toRemove).clear()
            historyPointer -= toRemove
            if (historyPointer < 0) historyPointer = 0
        }
    }

    data class GameState(
        val factionIndex: Int,
        val stageIndex: Int,
        val stepIndex: Int,
        val completedTurns: Int
    )
}