package com.github.firusv.smarti18n.action.treeview

import com.github.firusv.smarti18n.MessagesBundle
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.annotations.NotNull


/**
 * Разворачивание всех нод в дереве переводов (вкладка TreeView)
 * @author firus-v
 */
class ExpandTreeViewAction(private val expandRunnable: Runnable) :
    AnAction(
        MessagesBundle.message("view.tree.expand"),
        null, AllIcons.Actions.Expandall
    ) {
    override fun actionPerformed(@NotNull e: AnActionEvent) {
        expandRunnable.run()
    }
}