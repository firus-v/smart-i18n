package com.github.firusv.smarti18n.action

import com.github.firusv.smarti18n.MessagesBundle
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.github.firusv.smarti18n.settings.ProjectSettingsConfigurable

/**
 * Действие для открытия настроек плагина.
 * @author firus-v
 */
class SettingsAction : AnAction {

    constructor() : super(
        MessagesBundle.message("action.settings"),
        null, AllIcons.General.Settings
    )

    constructor(showIcon: Boolean) : super(
        MessagesBundle.message("action.settings"),
        null, if (showIcon) AllIcons.General.Settings else null
    )

    override fun actionPerformed(e: AnActionEvent) {
        ShowSettingsUtil.getInstance().showSettingsDialog(e.project, ProjectSettingsConfigurable::class.java)
    }
}
