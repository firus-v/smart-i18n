package com.github.firusv.smarti18n.assistance

import com.github.firusv.smarti18n.settings.ProjectSettingsService
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull

/**
 * Используется для определения хуков редактора
 * @author firus-v
 */
interface OptionalAssistance {
    fun isAssistance(@NotNull project: Project): Boolean {
        return ProjectSettingsService.get(project).state.getShowCodeAssistant()
    }
    fun isReference(@NotNull project: Project): Boolean {
        return ProjectSettingsService.get(project).state.getIsReferenceEnabled()
    }
}