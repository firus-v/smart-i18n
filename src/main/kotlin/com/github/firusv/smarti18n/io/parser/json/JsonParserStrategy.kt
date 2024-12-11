package com.github.firusv.smarti18n.io.parser.json

import com.github.firusv.smarti18n.exception.SyntaxException
import com.github.firusv.smarti18n.io.parser.ParserStrategy
import com.github.firusv.smarti18n.model.TranslationData
import com.github.firusv.smarti18n.model.TranslationFile
import com.github.firusv.smarti18n.model.TranslationNode
import com.github.firusv.smarti18n.settings.ProjectSettingsState
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NotNull
import java.io.InputStreamReader
import java.io.Reader
import java.util.*

/**
 * Стратегия парсинга для файлов в формате JSON.
 * @author firus-v
 */
class JsonParserStrategy(@NotNull settings: ProjectSettingsState) : ParserStrategy(settings) {

    companion object {
        private val GSON: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    }

    override fun read(@NotNull file: TranslationFile, @NotNull data: TranslationData) {
        data.addLocale(file.locale)

        val vf: VirtualFile = file.virtualFile
        val targetNode: TranslationNode = super.getOrCreateTargetNode(file, data)

        try {
            InputStreamReader(vf.inputStream, vf.charset).use { reader: Reader ->
                val input: JsonObject?

                try {
                    input = GSON.fromJson(reader, JsonObject::class.java)
                } catch (ex: JsonSyntaxException) {
                    throw SyntaxException(ex.message, file)
                }

                // @input будет null, если файл полностью пустой
                if (input != null) {
                    JsonMapper.read(file.locale, input, targetNode)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun write(@NotNull data: TranslationData, @NotNull file: TranslationFile): String {
        val targetNode: TranslationNode = super.getTargetNode(data, file)
        val output = JsonObject()
        JsonMapper.write(file.locale, output, Objects.requireNonNull(targetNode))
        return GSON.toJson(output)
    }
}