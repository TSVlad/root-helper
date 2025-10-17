package ru.tsvlad.root.helper

import javafx.application.Application
import javafx.stage.Stage
import ru.tsvlad.root.helper.view.GameView

class GameApp : Application() {
    override fun start(primaryStage: Stage) {
        val gameView = GameView()
        gameView.start(primaryStage)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(GameApp::class.java, *args)
        }
    }
}
