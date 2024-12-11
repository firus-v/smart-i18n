package com.github.firusv.smarti18n.service

import com.github.firusv.smarti18n.InstanceManager
import com.github.firusv.smarti18n.MessagesBundle
import com.github.firusv.smarti18n.action.*
import com.github.firusv.smarti18n.tabs.TableView
import com.github.firusv.smarti18n.tabs.TreeView
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import java.util.ArrayList


/**
 * Содает toolWindow панель плагина для работы с таблицей/деревом переводов
 * */
class TranslatorToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // Получаем singleton плагина
        val manager = InstanceManager.get(project)
        //Получаем экземпляр контент фабрики IDE
        val contentFactory = ContentFactory.getInstance()


        // Translations tree view
        val treeView = TreeView(project)
        val treeContent = contentFactory.createContent(
            treeView.getRootPanel(),
            MessagesBundle.message("view.tree.title"), false
        )

        toolWindow.contentManager.addContent(treeContent)

        // Translations table view
        val tableView = TableView(project)
        val tableContent: Content = contentFactory.createContent(
            tableView.getRootPanel(),
            MessagesBundle.message("view.table.title"), false
        )

        val actions: MutableList<AnAction> = ArrayList<AnAction>()
        actions.add(AddAction())
        actions.add(FilterIncompleteAction())
        actions.add(FilterDuplicateAction())
        actions.add(ReloadAction())
        actions.add(SettingsAction())
        actions.add(SearchAction { query -> manager.bus().propagate().onSearchQuery(query) })
        toolWindow.setTitleActions(actions)


        toolWindow.contentManager.addContent(tableContent)

        // Initialize Window Manager
        WindowManager.instance.initialize(toolWindow, treeView, tableView)

        // Синхронизируем UI с данными
        manager.uiBus().addListener(treeView)
        manager.uiBus().addListener(tableView)
        manager.bus().propagate().onUpdateData(manager.store().getData())
    }
}