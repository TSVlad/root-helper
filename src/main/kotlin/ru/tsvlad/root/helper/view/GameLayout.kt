package ru.tsvlad.root.helper.view

import javafx.geometry.Insets
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.geometry.Pos

class GameLayout {
    val root = BorderPane()
    val stepImage = ImageView().apply {
        isPreserveRatio = true
        isSmooth = true
    }
    val turnCounter = Label().apply {
        styleClass.add("turn-counter")
    }
    val topPanel = VBox(10.0, turnCounter).apply {
        alignment = Pos.CENTER
        padding = Insets(40.0, 0.0, 20.0, 0.0)
    }
    val stepImageContainer = StackPane(stepImage).apply {
        alignment = Pos.CENTER
        padding = Insets(0.0, 20.0, 10.0, 20.0)
    }

    init {
        // Корневой макет
        root.top = topPanel
        root.center = stepImageContainer
    }
}
