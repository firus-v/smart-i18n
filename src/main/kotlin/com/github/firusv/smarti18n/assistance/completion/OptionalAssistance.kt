package com.github.firusv.smarti18n.assistance.completion

import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull

/**
 * Used to define editor hooks as assistable.
 * @author marhali
 */
interface OptionalAssistance {
    fun isAssistance(@NotNull project: Project): Boolean {
        // TODO Добавить настройку, показывать подсказки или нет
        return true
    }
}