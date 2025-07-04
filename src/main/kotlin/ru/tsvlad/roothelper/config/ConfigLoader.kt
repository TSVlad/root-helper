//@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package ru.tsvlad.roothelper.config

import kotlinx.serialization.json.Json
import java.io.InputStream

object ConfigLoader {
    fun loadConfig(inputStream: InputStream): GameConfig {
        return Json { ignoreUnknownKeys = true }
            .decodeFromString<GameConfig>(inputStream.bufferedReader().use { it.readText() })
    }
}