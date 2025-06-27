package ru.tsvlad.roothelper.view

import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.stage.Stage
import ru.tsvlad.roothelper.config.ConfigLoader
import ru.tsvlad.roothelper.controller.GameController
import java.io.InputStream

class GameView : Application() {
    private lateinit var controller: GameController
    private lateinit var factionIcon: ImageView
    private lateinit var stageIcon: ImageView
    private lateinit var stepDescription: Label
    private lateinit var turnCounter: Label
    private lateinit var factionNameLabel: Label
    private lateinit var stageNameLabel: Label
    private lateinit var stepCounterLabel: Label
    private lateinit var primaryStage: Stage
    private lateinit var root: BorderPane
    private lateinit var topCenterPanel: VBox
    private lateinit var scene: Scene
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var descriptionContainer: StackPane
    private lateinit var descriptionScroll: ScrollPane

    override fun start(stage: Stage) {
        primaryStage = stage

        val configStream: InputStream = javaClass.getResourceAsStream(
            "/ru/tsvlad/roothelper/config.json"
        ) ?: throw IllegalStateException("Config file not found")
        val config = ConfigLoader.loadConfig(configStream)
        controller = GameController(config)

        // Создание UI элементов
        createUIElements()

        // Создание макета
        createLayout()

        // Настройка сцены
        scene = Scene(root, 1400.0, 900.0)
        scene.stylesheets.add(
            javaClass.getResource("/ru/tsvlad/roothelper/styles.css")?.toExternalForm()
        )

        // Обработка горячих клавиш
        scene.addEventHandler(KeyEvent.KEY_PRESSED) { event ->
            when (event.code) {
                KeyCode.F11 -> {
                    primaryStage.isFullScreen = !primaryStage.isFullScreen
                    event.consume()
                }
                KeyCode.RIGHT, KeyCode.D -> {
                    updateState(true)
                    event.consume()
                }
                KeyCode.LEFT, KeyCode.A -> {
                    updateState(false)
                    event.consume()
                }
                else -> {}
            }
        }

        primaryStage.title = "Root Game Helper"
        primaryStage.scene = scene
        primaryStage.isResizable = true

        // Привязка размеров иконок к размеру окна
        bindIconSizes()

        // Добавляем слушатели для автоматического изменения размера текста
        setupAutoResizeText()

        primaryStage.show()
        updateUI()

        // Установка фокуса для обработки клавиатуры
        root.requestFocus()
    }

    private fun createUIElements() {
        // Создаем ImageView без фиксированных размеров
        factionIcon = ImageView().apply {
            isPreserveRatio = true
            isSmooth = true
        }

        stageIcon = ImageView().apply {
            isPreserveRatio = true
            isSmooth = true
        }

        // Создаем Label для описания с возможностью переноса слов
        stepDescription = Label().apply {
            styleClass.add("step-description")
            isWrapText = true
            alignment = Pos.CENTER
            textAlignment = TextAlignment.CENTER
            maxWidth = Double.MAX_VALUE
        }

        turnCounter = Label().apply {
            styleClass.add("turn-counter")
        }

        factionNameLabel = Label().apply {
            styleClass.add("faction-name")
        }

        stageNameLabel = Label().apply {
            styleClass.add("stage-name")
        }

        stepCounterLabel = Label().apply {
            styleClass.add("step-counter")
        }

        prevButton = Button("←").apply {
            styleClass.addAll("btn", "btn-secondary")
            setOnAction { updateState(false) }
        }

        nextButton = Button("→").apply {
            styleClass.addAll("btn", "btn-success")
            setOnAction { updateState(true) }
        }
    }

    private fun bindIconSizes() {
        // Увеличиваем размер иконки фракции до 25% высоты окна
        factionIcon.fitHeightProperty().bind(
            Bindings.createDoubleBinding(
                { scene.height * 0.25 }, // Было 0.20
                scene.heightProperty()
            )
        )
        factionIcon.fitWidthProperty().bind(factionIcon.fitHeightProperty())

        // Увеличиваем размер иконки этапа до 20% высоты окна
        stageIcon.fitHeightProperty().bind(
            Bindings.createDoubleBinding(
                { scene.height * 0.20 }, // Было 0.15
                scene.heightProperty()
            )
        )
        stageIcon.fitWidthProperty().bind(stageIcon.fitHeightProperty())

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

        // Привязка максимальной ширины описания
        stepDescription.maxWidthProperty().bind(root.widthProperty().multiply(0.8))
    }

    private fun createLayout() {
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

        // Центральная верхняя панель (информация)
        // Добавляем название этапа между названием фракции и счетчиком шага
        topCenterPanel = VBox(10.0, turnCounter, factionNameLabel, stageNameLabel, stepCounterLabel).apply {
            alignment = Pos.CENTER
            padding = Insets(40.0, 0.0, 40.0, 0.0)
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

        // Контейнер для описания с возможностью прокрутки
        // Центрируем содержимое ScrollPane
        val centeredDescription = StackPane(stepDescription).apply {
            alignment = Pos.CENTER
        }

        descriptionScroll = ScrollPane().apply {
            content = centeredDescription
            isFitToWidth = true
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            style = "-fx-background: transparent; -fx-background-color: transparent;"
            minViewportHeight = 200.0
        }

        // Центральная панель (описание шага)
        descriptionContainer = StackPane(descriptionScroll).apply {
            alignment = Pos.CENTER
            padding = Insets(20.0, 60.0, 20.0, 60.0)
        }

        // Корневой макет
        root = BorderPane().apply {
            top = topPanel
            center = descriptionContainer
            bottom = bottomPanel
        }
    }

    private fun setupAutoResizeText() {
        // Добавляем слушатели только после инициализации сцены
        stepDescription.textProperty().addListener { _, _, _ ->
            if (::scene.isInitialized) adjustFontSize()
        }
        root.widthProperty().addListener { _, _, _ ->
            if (::scene.isInitialized) adjustFontSize()
        }
        root.heightProperty().addListener { _, _, _ ->
            if (::scene.isInitialized) adjustFontSize()
        }
    }

    private fun adjustFontSize() {
        if (!::descriptionContainer.isInitialized || !::stepDescription.isInitialized) return

        val baseSize = scene.height * 0.025
        var fontSize = baseSize * 1.5

        // Создаем тестовый текст для измерения
        val testText = Text(stepDescription.text)
        testText.wrappingWidth = stepDescription.maxWidth
        testText.font = Font.font("Arial", FontWeight.BOLD, fontSize)

        // Доступная высота с учетом отступов
        val availableHeight = descriptionContainer.height - 40

        // Пока текст не помещается, уменьшаем размер шрифта
        while (testText.layoutBounds.height > availableHeight && fontSize > 10) {
            fontSize -= 0.5
            testText.font = Font.font("Arial", FontWeight.BOLD, fontSize)
        }

        stepDescription.font = Font.font("Arial", FontWeight.BOLD, fontSize)
    }

    private fun updateState(isNext: Boolean) {
        if (isNext) controller.nextStep() else controller.prevStep()
        updateUI()
    }

    private fun updateUI() {
        val (faction, stage, step) = controller.currentState()

        // Загрузка изображений
        factionIcon.image = loadImage(faction.icon)
        stageIcon.image = loadImage(stage.icon)

        // Установка текстов
        stepDescription.text = step.description
        turnCounter.text = "Ход: ${controller.getCurrentTurn()}"
        factionNameLabel.text = faction.name
        stageNameLabel.text = stage.name

        // Расчет номера шага
        val stepIndex = stage.steps.indexOf(step) + 1
        stepCounterLabel.text = "Шаг: $stepIndex/${stage.steps.size}"

        // Установка цвета фона
        val backgroundColor = faction.color
        root.style = "-fx-background-color: $backgroundColor;"

        // Расчет контрастного цвета текста
        val textColor = getContrastColor(backgroundColor)
        applyTextColor(textColor)

        // Обновляем размер шрифта после изменения текста
        if (::scene.isInitialized) {
            adjustFontSize()
        }
    }

    private fun loadImage(path: String): Image {
        val resource = javaClass.getResource("/images/$path")
            ?: throw RuntimeException("Resource not found: $path")
        return Image(resource.toExternalForm())
    }

    private fun getContrastColor(hexColor: String): String {
        val color = hexColor.replace("#", "")
        if (color.length < 6) return "#FFFFFF"

        val r = color.substring(0, 2).toIntOrNull(16) ?: 0
        val g = color.substring(2, 4).toIntOrNull(16) ?: 0
        val b = color.substring(4, 6).toIntOrNull(16) ?: 0

        val brightness = (0.299 * r + 0.587 * g + 0.114 * b) / 255
        return if (brightness > 0.5) "#000000" else "#FFFFFF"
    }

    private fun applyTextColor(colorHex: String) {
        val style = "-fx-text-fill: $colorHex;"

        turnCounter.style = style
        factionNameLabel.style = style
        stageNameLabel.style = style
        stepCounterLabel.style = style
        stepDescription.style = style
    }
}