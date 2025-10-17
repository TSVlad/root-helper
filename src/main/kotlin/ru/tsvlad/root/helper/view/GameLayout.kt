package ru.tsvlad.root.helper.view

import javafx.geometry.Insets
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.geometry.Pos
import javafx.scene.text.TextAlignment

class GameLayout {
    val root = BorderPane()
    val factionIcon = ImageView().apply {
        isPreserveRatio = true
        isSmooth = true
    }
    val stageIcon = ImageView().apply {
        isPreserveRatio = true
        isSmooth = true
    }
    val stepDescription = Label().apply {
        styleClass.add("step-description")
        isWrapText = true
        alignment = Pos.CENTER
        textAlignment = TextAlignment.CENTER
        maxWidth = Double.MAX_VALUE
    }
    val turnCounter = Label().apply {
        styleClass.add("turn-counter")
    }
    val factionNameLabel = Label().apply {
        styleClass.add("faction-name")
    }
    val stageNameLabel = Label().apply {
        styleClass.add("stage-name")
    }
    val stepCounterLabel = Label().apply {
        styleClass.add("step-counter")
    }
    val prevButton = Button("←").apply {
        styleClass.addAll("btn", "btn-secondary")
    }
    val nextButton = Button("→").apply {
        styleClass.addAll("btn", "btn-success")
    }
    val topCenterPanel = VBox(10.0, turnCounter, factionNameLabel, stageNameLabel, stepCounterLabel).apply {
        alignment = Pos.CENTER
        padding = Insets(40.0, 0.0, 40.0, 0.0)
    }
    val descriptionScroll = ScrollPane().apply {
        isFitToWidth = true
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        style = "-fx-background: transparent; -fx-background-color: transparent;"
        minViewportHeight = 200.0
    }
    val descriptionContainer = StackPane(descriptionScroll).apply {
        alignment = Pos.CENTER
        padding = Insets(20.0, 60.0, 20.0, 60.0)
    }

    init {
        // Верхняя левая панель (иконка фракции)
        val topLeft = StackPane(factionIcon).apply {
            alignment = Pos.TOP_LEFT
            padding = Insets(40.0)
        }

        // Верхняя правая панель (только иконка этапа)
        val topRight = StackPane(stageIcon).apply {
            alignment = Pos.TOP_RIGHT
            padding = Insets(40.0)
        }

        // Верхняя панель (объединяем все)
        val topPanel = BorderPane().apply {
            left = topLeft
            center = topCenterPanel
            right = topRight
        }

        // Нижняя панель (навигация)
        val navigation = HBox(50.0, prevButton, nextButton).apply {
            alignment = Pos.CENTER
            padding = Insets(40.0)
        }

        val bottomPanel = StackPane(navigation).apply {
            alignment = Pos.BOTTOM_CENTER
        }

        // Центрируем содержимое ScrollPane
        val centeredDescription = StackPane(stepDescription).apply {
            alignment = Pos.CENTER
        }
        descriptionScroll.content = centeredDescription

        // Корневой макет
        root.top = topPanel
        root.center = descriptionContainer
        root.bottom = bottomPanel
    }
}
