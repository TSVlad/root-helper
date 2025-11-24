package ru.tsvlad.root.helper.view

import javafx.beans.binding.Bindings
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.Stage

class GameControls(
    private val primaryStage: Stage,
    private val scene: Scene,
    private val turnCounter: Label
) {
    fun setupBindings() {
        // Размер шрифта счетчика хода привязываем к размеру окна
        val baseFontSize = Bindings.createDoubleBinding(
            { scene.height * 0.025 },
            scene.heightProperty()
        )

        turnCounter.fontProperty().bind(Bindings.createObjectBinding(
            { Font.font("Arial", FontWeight.BOLD, baseFontSize.get() * 1.8) },
            baseFontSize
        ))
    }

    fun setupKeyboardHandlers(onNext: () -> Unit, onPrev: () -> Unit) {
        // Обработка горячих клавиш
        scene.addEventHandler(KeyEvent.KEY_PRESSED) { event ->
            when (event.code) {
                KeyCode.F11 -> {
                    primaryStage.isFullScreen = !primaryStage.isFullScreen
                    event.consume()
                }
                KeyCode.RIGHT, KeyCode.D -> {
                    onNext()
                    event.consume()
                }
                KeyCode.LEFT, KeyCode.A -> {
                    onPrev()
                    event.consume()
                }
                else -> {}
            }
        }
    }
}
