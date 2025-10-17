package ru.tsvlad.root.helper.view

import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import javafx.stage.Stage
import ru.tsvlad.root.helper.config.ConfigLoader
import ru.tsvlad.root.helper.config.FractionConfig
import ru.tsvlad.root.helper.config.GameConfig
import ru.tsvlad.root.helper.controller.GameController
import java.io.InputStream

class GameView {
    private lateinit var controller: GameController
    private lateinit var primaryStage: Stage
    private lateinit var scene: Scene
    private lateinit var config: GameConfig
    private val selectedFactions = FXCollections.observableArrayList<FractionConfig>()
    private val gameLayout = GameLayout()
    private val gameControls: GameControls
        get() = GameControls(primaryStage, scene, gameLayout.prevButton, gameLayout.nextButton, gameLayout.turnCounter, gameLayout.factionNameLabel, gameLayout.stageNameLabel, gameLayout.stepCounterLabel)
    fun start(primaryStage: Stage) {
        this.primaryStage = primaryStage

        val configStream: InputStream = javaClass.getResourceAsStream(
            "/ru/tsvlad/root/helper/config.json"
        ) ?: throw IllegalStateException("Config file not found")
        config = ConfigLoader.loadConfig(configStream)

        // Показываем стартовый экран
        val startScreen = StartScreen(config) { selectedFactions ->
            startGame(selectedFactions)
        }
        startScreen.show(primaryStage)
    }
    private fun startGame(selectedFactions: List<FractionConfig>) {
        // Создаем контроллер с выбранными фракциями (в правильном порядке)
        controller = GameController(GameConfig(selectedFactions, config.maxHistorySize))

        // Создание UI элементов
        createUIElements()

        // Настройка сцены
        scene = Scene(gameLayout.root, 1400.0, 900.0)
        scene.stylesheets.add(
            javaClass.getResource("/ru/tsvlad/root/helper/styles.css")?.toExternalForm()
        )

        // Создание макета после инициализации сцены
        createLayout()

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

        updateUI()

        // Установка фокуса для обработки клавиатуры
        gameLayout.root.requestFocus()
    }
    private fun createUIElements() {
        // Создаем ImageView без фиксированных размеров
        gameLayout.factionIcon.apply {
            isPreserveRatio = true
            isSmooth = true
        }

        gameLayout.stageIcon.apply {
            isPreserveRatio = true
            isSmooth = true
        }

        // Создаем Label для описания с возможностью переноса слов
        gameLayout.stepDescription.apply {
            styleClass.add("step-description")
            isWrapText = true
            alignment = Pos.CENTER
            textAlignment = TextAlignment.CENTER
            maxWidth = Double.MAX_VALUE
        }

        gameLayout.turnCounter.apply {
            styleClass.add("turn-counter")
        }

        gameLayout.factionNameLabel.apply {
            styleClass.add("faction-name")
        }

        gameLayout.stageNameLabel.apply {
            styleClass.add("stage-name")
        }

        gameLayout.stepCounterLabel.apply {
            styleClass.add("step-counter")
        }

        gameLayout.prevButton.apply {
            styleClass.addAll("btn", "btn-secondary")
            setOnAction { updateState(false) }
        }

        gameLayout.nextButton.apply {
            styleClass.addAll("btn", "btn-success")
            setOnAction { updateState(true) }
        }
    }
    private fun bindIconSizes() {
        // Увеличиваем размер иконки фракции до 25% высоты окна
        gameLayout.factionIcon.fitHeightProperty().bind(
            Bindings.createDoubleBinding(
                { scene.height * 0.25 }, // Было 0.20
                scene.heightProperty()
            )
        )
        gameLayout.factionIcon.fitWidthProperty().bind(gameLayout.factionIcon.fitHeightProperty())

        // Увеличиваем размер иконки этапа до 20% высоты окна
        gameLayout.stageIcon.fitHeightProperty().bind(
            Bindings.createDoubleBinding(
                { scene.height * 0.20 }, // Было 0.15
                scene.heightProperty()
            )
        )
        gameLayout.stageIcon.fitWidthProperty().bind(gameLayout.stageIcon.fitHeightProperty())

        // Привязка максимальной ширины описания
        gameLayout.stepDescription.maxWidthProperty().bind(gameLayout.root.widthProperty().multiply(0.8))
    }
    private fun createLayout() {
        // Устанавливаем обработчики клавиш
        gameControls.setupKeyboardHandlers(
            onNext = { updateState(true) },
            onPrev = { updateState(false) }
        )

        // Настраиваем привязки
        gameControls.setupBindings()
    }
    private fun setupAutoResizeText() {
        // Добавляем слушатели только после инициализации сцены
        gameLayout.stepDescription.textProperty().addListener { _, _, _ ->
            if (::scene.isInitialized) adjustFontSize()
        }
        gameLayout.root.widthProperty().addListener { _, _, _ ->
            if (::scene.isInitialized) adjustFontSize()
        }
        gameLayout.root.heightProperty().addListener { _, _, _ ->
            if (::scene.isInitialized) adjustFontSize()
        }
    }
    private fun adjustFontSize() {
        if (!::scene.isInitialized) return

        val baseSize = scene.height * 0.025
        var fontSize = baseSize * 1.5

        // Создаем тестовый текст для измерения
        val testText = Text(gameLayout.stepDescription.text)
        testText.wrappingWidth = gameLayout.stepDescription.maxWidth
        testText.font = Font.font("Arial", FontWeight.BOLD, fontSize)

        // Доступная высота с учетом отступов
        val availableHeight = gameLayout.descriptionContainer.height - 40

        // Пока текст не помещается, уменьшаем размер шрифта
        while (testText.layoutBounds.height > availableHeight && fontSize > 10) {
            fontSize -= 0.5
            testText.font = Font.font("Arial", FontWeight.BOLD, fontSize)
        }

        gameLayout.stepDescription.font = Font.font("Arial", FontWeight.BOLD, fontSize)
    }
    private fun updateState(isNext: Boolean) {
        if (isNext) controller.nextStep() else controller.prevStep()
        updateUI()
    }
    private fun updateUI() {
        val (faction, stage, step) = controller.currentState()

        // Загрузка изображений
        gameLayout.factionIcon.image = loadImage(faction.icon)
        gameLayout.stageIcon.image = loadImage(stage.icon)

        // Установка текстов
        gameLayout.stepDescription.text = step.description
        gameLayout.turnCounter.text = "Ход: ${controller.getCurrentTurn()}"
        gameLayout.factionNameLabel.text = faction.name
        gameLayout.stageNameLabel.text = stage.name

        // Расчет номера шага
        val stepIndex = stage.steps.indexOf(step) + 1
        gameLayout.stepCounterLabel.text = "Шаг: $stepIndex/${stage.steps.size}"

        // Установка цвета фона
        val backgroundColor = faction.color
        gameLayout.root.style = "-fx-background-color: $backgroundColor;"

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

        gameLayout.turnCounter.style = style
        gameLayout.factionNameLabel.style = style
        gameLayout.stageNameLabel.style = style
        gameLayout.stepCounterLabel.style = style
        gameLayout.stepDescription.style = style
    }

    // Устаревшие методы удалены
}
