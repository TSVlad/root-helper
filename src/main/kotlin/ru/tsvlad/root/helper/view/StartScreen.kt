package ru.tsvlad.root.helper.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.collections.FXCollections
import javafx.stage.Stage
import ru.tsvlad.root.helper.config.FractionConfig
import ru.tsvlad.root.helper.config.GameConfig

class StartScreen(
    private val gameConfig: GameConfig,
    private val onGameStart: (List<FractionConfig>) -> Unit
) {
    private val selectedFactions = FXCollections.observableArrayList<FractionConfig>()

    fun show(primaryStage: Stage) {
        val root = VBox(20.0)
        root.alignment = Pos.CENTER
        root.padding = Insets(40.0)
        root.styleClass.add("start-screen")

        val title = Label("Выберите фракции для игры")
        title.styleClass.add("start-title")

        val factionsGrid = GridPane()
        factionsGrid.alignment = Pos.CENTER
        factionsGrid.hgap = 20.0
        factionsGrid.vgap = 20.0
        factionsGrid.padding = Insets(20.0)
        factionsGrid.styleClass.add("factions-grid")

        // Создаем чекбоксы для выбора фракций
        val factionCheckboxes = mutableListOf<CheckBox>()
        val factionLabels = mutableMapOf<FractionConfig, Label>()

        gameConfig.fractions.forEachIndexed { index, faction ->
            val factionBox = VBox(10.0)
            factionBox.alignment = Pos.CENTER
            factionBox.styleClass.add("faction-box")

            val imageView = ImageView(loadImage(faction.icon)).apply {
                fitHeight = 120.0
                fitWidth = 120.0
                isPreserveRatio = true
            }

            val label = Label(faction.name).apply {
                styleClass.add("faction-label")
            }
            factionLabels[faction] = label

            val checkbox = CheckBox().apply {
                styleClass.add("faction-checkbox")
                isSelected = true
                selectedFactions.add(faction)
                selectedProperty().addListener { _, _, isSelected ->
                    if (isSelected) {
                        selectedFactions.add(faction)
                    } else {
                        selectedFactions.remove(faction)
                    }
                    updateFactionLabels(factionLabels, selectedFactions)
                }
            }

            factionBox.children.addAll(imageView, label, checkbox)
            factionCheckboxes.add(checkbox)

            factionsGrid.add(factionBox, index % 3, index / 3)
        }

        // Обновляем метки после создания всех элементов
        updateFactionLabels(factionLabels, selectedFactions)

        val startButton = Button("Начать игру").apply {
            styleClass.addAll("btn", "btn-primary", "btn-start")
        }

        // Обработчик для кнопки
        startButton.setOnAction {
            if (selectedFactions.isEmpty()) {
                Alert(Alert.AlertType.WARNING, "Выберите хотя бы одну фракцию").show()
            } else {
                onGameStart(selectedFactions.toList())
            }
        }

        root.children.addAll(title, factionsGrid, startButton)

        val scene = Scene(root, 1200.0, 800.0)
        scene.stylesheets.add(
            javaClass.getResource("/ru/tsvlad/root/helper/styles.css")?.toExternalForm()
        )

        primaryStage.title = "Root Game Helper - Выбор фракций"
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun loadImage(path: String): Image {
        val resource = javaClass.getResource("/images/$path")
            ?: throw RuntimeException("Resource not found: $path")
        return Image(resource.toExternalForm())
    }

    private fun updateFactionLabels(
        factionLabels: Map<FractionConfig, Label>,
        selectedFactions: List<FractionConfig>
    ) {
        gameConfig.fractions.forEach { faction ->
            val label = factionLabels[faction] ?: return@forEach
            val indexInSelected = selectedFactions.indexOf(faction)
            if (indexInSelected >= 0) {
                // Фракция выбрана - показываем номер
                label.text = "${indexInSelected + 1}. ${faction.name}"
            } else {
                // Фракция не выбрана - показываем только название
                label.text = faction.name
            }
        }
    }
}
