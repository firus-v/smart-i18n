package com.github.firusv.smarti18n.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.github.firusv.smarti18n.InstanceManager
import com.github.firusv.smarti18n.MessagesBundle

/**
 * Действие для перезагрузки переводов.
 * @author firus-v
 */
class ReloadAction : AnAction(
    MessagesBundle.message("action.reload"),
    null, AllIcons.Actions.Refresh
) {

    override fun actionPerformed(e: AnActionEvent) {
        InstanceManager.get(e.project ?: return).reload()
    }
}
