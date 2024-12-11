package com.github.firusv.smarti18n.assistance.completion

import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull

/**
 * Используется для определения хуков редактора
 * @author firus-v
 */
interface OptionalAssistance {
    fun isAssistance(@NotNull project: Project): Boolean {
        // TODO Добавить настройку, показывать подсказки или нет
        return true
    }
}