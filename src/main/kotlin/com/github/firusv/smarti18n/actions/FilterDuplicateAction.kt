package com.github.firusv.smarti18n.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.github.firusv.smarti18n.InstanceManager
import com.github.firusv.smarti18n.MessagesBundle

/**
 * Действие для переключения фильтра дублирующихся значений перевода.
 * @author firus-v
 */
class FilterDuplicateAction : AnAction(
    MessagesBundle.message("action.filter.duplicate"),
    null, AllIcons.Actions.PreserveCase
) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val enable = e.presentation.icon == AllIcons.Actions.PreserveCase
        e.presentation.icon = if (enable) AllIcons.Actions.PreserveCaseSelected else AllIcons.Actions.PreserveCase
        InstanceManager.get(project).bus().propagate().onFilterDuplicate(enable)
    }
}
