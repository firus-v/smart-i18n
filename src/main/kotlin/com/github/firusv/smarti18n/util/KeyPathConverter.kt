package com.github.firusv.smarti18n.util

import com.github.firusv.smarti18n.model.KeyPath
import com.github.firusv.smarti18n.settings.ProjectSettingsService
import com.github.firusv.smarti18n.settings.ProjectSettingsState
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull
import java.util.regex.Pattern

/**
 * Утилита с состоянием для преобразования абсолютных ключей перевода в их строковое представление и обратно.
 * @author firus-v
 */
class KeyPathConverter {

    private val settings: ProjectSettingsState

    /**
     * Создает новый экземпляр конвертера.
     * @param settings Конфигурация разделителей
     */
    constructor(settings: ProjectSettingsState) {
        this.settings = settings
    }

    /**
     * @see #KeyPathConverter(ProjectSettings)
     * @param project Открытый проект
     */
    constructor(project: Project) : this(ProjectSettingsService.get(project).state)

    /**
     * Преобразование в строковое представление.
     * @param path Абсолютный путь ключа
     * @return Строковое представление
     */
    fun toString(@NotNull path: KeyPath): String {
        val builder = StringBuilder()

        for (i in 0 until path.size) {
            if (i > 0 && i < path.size) { // Разделители
                builder.append(settings.getDelimiter())
            }

            // Содержимое секции
            builder.append(path[i])
        }

        return builder.toString()
    }

    /**
     * Разделяет строку на секции пути ключа.
     * Если активирован режим пространства имен и оно не указано, добавляется пространство имен по умолчанию.
     * @return Секции пути ключа
     */
    fun fromString(@NotNull literalPath: String): KeyPath {
        val path = KeyPath()
        var i = 0
        val list = literalPath.split(settings.getDelimiter())

        for (section in list) {
            path.add(section)
            i++
        }

        return path
    }

    override fun toString(): String {
        return "KeyPathConverter(settings=$settings)"
    }

    /*
     * ВНУТРЕННИЕ МЕТОДЫ
     */

    private fun getSplitRegex(): String {
        return Pattern.quote("\\") + settings.getDelimiter()
    }

    private fun getSplitCharsRegex(): String {
        val builder = StringBuilder()

        builder.append("(")
        builder.append(Pattern.quote(settings.getDelimiter()))
        builder.append(")")
        return builder.toString()
    }


}