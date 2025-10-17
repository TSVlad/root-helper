package ru.tsvlad.root.helper.view

import javafx.beans.binding.Bindings
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.Stage

class GameControls(
    private val primaryStage: Stage,
    private val scene: Scene,
    private val prevButton: Button,
    private val nextButton: Button,
    private val turnCounter: Label,
    private val factionNameLabel: Label,
    private val stageNameLabel: Label,
    private val stepCounterLabel: Label
) {
    fun setupBindings() {
        // Привязка размера кнопок
        prevButton.prefHeightProperty().bind(
            Bindings.createDoubleBinding(
                { scene.height * 0.08 },
                scene.heightProperty()
            )
        )
        prevButton.prefWidthProperty().bind(prevButton.prefHeightProperty().multiply(2.0))

        nextButton.prefHeightProperty().bind(
            Bindings.createDoubleBinding(
                { scene.height * 0.08 },
                scene.heightProperty()
            )
        )
        nextButton.prefWidthProperty().bind(nextButton.prefHeightProperty().multiply(2.0))

        // Привязка шрифта кнопок
        val buttonFontBinding = Bindings.createObjectBinding(
            { Font.font(scene.height * 0.04) },
            scene.heightProperty()
        )

        prevButton.fontProperty().bind(buttonFontBinding)
        nextButton.fontProperty().bind(buttonFontBinding)

        // Размер шрифтов также привязываем к размеру окна
        val baseFontSize = Bindings.createDoubleBinding(
            { scene.height * 0.025 },
            scene.heightProperty()
        )

        turnCounter.fontProperty().bind(Bindings.createObjectBinding(
            { Font.font("Arial", FontWeight.BOLD, baseFontSize.get() * 1.8) },
            baseFontSize
        ))

        factionNameLabel.fontProperty().bind(Bindings.createObjectBinding(
            { Font.font("Arial", FontWeight.BOLD, baseFontSize.get() * 1.6) },
            baseFontSize
        ))

        stageNameLabel.fontProperty().bind(Bindings.createObjectBinding(
            { Font.font("Arial", FontWeight.NORMAL, baseFontSize.get() * 1.4) },
            baseFontSize
        ))

        stepCounterLabel.fontProperty().bind(Bindings.createObjectBinding(
            { Font.font("Arial", FontWeight.NORMAL, baseFontSize.get() * 1.2) },
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
