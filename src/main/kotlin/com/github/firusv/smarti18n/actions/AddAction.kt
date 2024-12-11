package com.github.firusv.smarti18n.action

import com.github.firusv.smarti18n.MessagesBundle
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.github.firusv.smarti18n.dialog.AddDialog
import com.github.firusv.smarti18n.model.KeyPath
import com.github.firusv.smarti18n.service.WindowManager
import com.github.firusv.smarti18n.util.KeyPathConverter
import com.github.firusv.smarti18n.util.TreeUtil

/**
 * Действие для добавления перевода.
 * @author firus-v
 */
class AddAction : AnAction(
    MessagesBundle.message("action.add"),
    null, AllIcons.General.Add
) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        AddDialog(project, detectPreKey(project), null).showAndHandle()
    }

    /**
     * Определяет выбранный ключ перевода в нашем инструменте.
     * @param project Открытый проект
     * @return Найденный ключ для предварительного заполнения перевода или null, если это не применимо
     */
    private fun detectPreKey(project: Project): KeyPath? {
        val converter = KeyPathConverter(project)
        val window = WindowManager.instance

        if (window.toolWindow == null) {
            return null
        }

        val manager = window.toolWindow?.contentManager?.selectedContent ?: return null

        if (manager.displayName == MessagesBundle.message("view.tree.title")) { // Tree View
            val path = window.getTreeView()?.getTree()?.selectionPath
            if (path != null) {
                return TreeUtil.getFullPath(path)
            }
        } else { // Table View
            val row = window.getTableView()?.getTable()?.selectedRow ?: 0
            if (row > 0) {
                val path = window.getTableView()?.getTable()?.getValueAt(row, 0).toString()
                return converter.fromString(path)
            }
        }

        return null
    }
}
