package com.github.firusv.smarti18n.service

import com.intellij.openapi.wm.ToolWindow
import com.github.firusv.smarti18n.tabs.TableView
import com.github.firusv.smarti18n.tabs.TreeView

/**
 * Предоставляет доступ tool-window плагина
 * @author firus-v
 */
class WindowManager private constructor() {
    var toolWindow: ToolWindow? = null
        private set
    private var treeView: TreeView? = null
    private var tableView: TableView? = null

    fun initialize(toolWindow: ToolWindow?, treeView: TreeView?, tableView: TableView?) {
        this.toolWindow = toolWindow
        this.treeView = treeView
        this.tableView = tableView
    }

    fun getTreeView(): TreeView? {
        return treeView
    }

    fun getTableView(): TableView? {
        return tableView
    }

    companion object {
        private var INSTANCE: WindowManager? = null

        val instance: WindowManager
            get() = if (INSTANCE == null) WindowManager().also { INSTANCE = it } else INSTANCE!!
    }
}