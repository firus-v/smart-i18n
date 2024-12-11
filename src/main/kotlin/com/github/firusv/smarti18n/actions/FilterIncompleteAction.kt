package com.github.firusv.smarti18n.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.github.firusv.smarti18n.InstanceManager
import com.github.firusv.smarti18n.MessagesBundle

/**
 * Действие для переключения фильтра перевода по отсутствующим значениям.
 * @author firus-v
 */
class FilterIncompleteAction : AnAction(
    MessagesBundle.message("action.filter.incomplete"),
    null, AllIcons.Actions.Words
) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val enable = e.presentation.icon == AllIcons.Actions.Words
        e.presentation.icon = if (enable) AllIcons.Actions.WordsSelected else AllIcons.Actions.Words
        InstanceManager.get(project).bus().propagate().onFilterIncomplete(enable)
    }
}
