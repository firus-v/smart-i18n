package com.github.firusv.smarti18n.action.treeview

import com.github.firusv.smarti18n.MessagesBundle
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.annotations.NotNull


/**
 * Action to collapse all tree nodes with children.
 * @author firusv
 */
class CollapseTreeViewAction(private val collapseRunnable: Runnable) :
    AnAction(
        MessagesBundle.message("view.tree.collapse"),
        null, AllIcons.Actions.Collapseall
    ) {
    override fun actionPerformed(@NotNull e: AnActionEvent) {
        collapseRunnable.run()
    }
}