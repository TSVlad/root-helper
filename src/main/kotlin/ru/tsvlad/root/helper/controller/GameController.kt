package ru.tsvlad.root.helper.controller

import ru.tsvlad.root.helper.config.FractionConfig
import ru.tsvlad.root.helper.config.GameConfig
import ru.tsvlad.root.helper.config.StepConfig
import ru.tsvlad.root.helper.model.GameState

class GameController(private val config: GameConfig) {
    private val factions = config.fractions
    private var currentFactionIndex = 0
    private var currentStepIndex = 0
    private var completedTurns = 0
    private val history = mutableListOf<GameState>()
    private var historyPointer = 0 // Указатель на текущее состояние в истории

    init {
        // Сохраняем начальное состояние
        saveState()
    }

    fun currentState(): Pair<FractionConfig, StepConfig> {
        val state = history[historyPointer]
        val fraction = factions[state.factionIndex]
        val step = fraction.steps[state.stepIndex]
        return Pair(fraction, step)
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
        var newStepIndex = currentState.stepIndex
        var newTurns = currentState.completedTurns

        val faction = factions[newFactionIndex]

        if (newStepIndex < faction.steps.size - 1) {
            newStepIndex++
        } else {
            newStepIndex = 0
            newFactionIndex = (newFactionIndex + 1) % factions.size
            if (newFactionIndex == 0) {
                newTurns++
            }
        }

        // Сохраняем новое состояние
        saveState(GameState(newFactionIndex, newStepIndex, newTurns))
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
}