package ru.tsvlad.root.helper.view

import javafx.beans.binding.Bindings
import javafx.scene.Scene
import javafx.scene.image.Image
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
    private val gameLayout = GameLayout()
    private val gameControls: GameControls
        get() = GameControls(primaryStage, scene, gameLayout.turnCounter)
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

        primaryStage.title = "Root Game Helper"
        primaryStage.scene = scene
        primaryStage.isResizable = true

        // Привязка размера изображения шага к размеру окна
        bindStepImageSize()

        updateUI()

        // Установка фокуса для обработки клавиатуры
        gameLayout.root.isFocusTraversable = true
        gameLayout.root.requestFocus()

        // Восстановление фокуса при клике по сцене
        gameLayout.root.setOnMouseClicked {
            gameLayout.root.requestFocus()
        }
    }
    private fun createUIElements() {
        // Настройка ImageView для изображения шага
        gameLayout.stepImage.apply {
            isPreserveRatio = true
            isSmooth = true
        }

        gameLayout.turnCounter.apply {
            styleClass.add("turn-counter")
        }
    }
    private fun bindStepImageSize() {
        // Привязка размера изображения шага к размеру окна
        // Используем 95% от минимального размера (ширина или высота) для сохранения пропорций
        // Привязываем только высоту, ширина будет автоматически подстраиваться благодаря isPreserveRatio
        gameLayout.stepImage.fitHeightProperty().bind(
            Bindings.createDoubleBinding(
                { 
                    val minSize = minOf(scene.width * 0.95, scene.height * 0.85)
                    minSize
                },
                scene.widthProperty(),
                scene.heightProperty()
            )
        )
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
    private fun updateState(isNext: Boolean) {
        if (isNext) controller.nextStep() else controller.prevStep()
        updateUI()
    }
    private fun updateUI() {
        val (faction, step) = controller.currentState()

        // Загрузка изображения шага
        gameLayout.stepImage.image = loadImage(step.picture)

        // Установка счетчика хода
        gameLayout.turnCounter.text = "Ход: ${controller.getCurrentTurn()}"

        // Установка цвета фона
        val backgroundColor = faction.color
        gameLayout.root.style = "-fx-background-color: $backgroundColor;"

        // Расчет контрастного цвета текста для счетчика хода
        val textColor = getContrastColor(backgroundColor)
        gameLayout.turnCounter.style = "-fx-text-fill: $textColor;"
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
}
