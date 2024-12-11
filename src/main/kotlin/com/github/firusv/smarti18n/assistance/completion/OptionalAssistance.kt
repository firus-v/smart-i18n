package com.github.firusv.smarti18n.assistance.completion

import com.github.firusv.smarti18n.settings.ProjectSettingsService
import com.intellij.ide.startup.importSettings.data.SettingsService
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
}