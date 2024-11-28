package com.github.firusv.smarti18n.service

import com.github.firusv.smarti18n.InstanceManager
import com.github.firusv.smarti18n.MessagesBundle
import com.github.firusv.smarti18n.tabs.TableView
import com.github.firusv.smarti18n.tabs.TreeView
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import java.util.*

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
        val treeView: TreeView = TreeView(project)
        val treeContent = contentFactory.createContent(
            treeView.getRootPanel(),
            MessagesBundle.message("view.tree.title"), false
        )

        toolWindow.contentManager.addContent(treeContent)

        // Translations table view
        val tableView: TableView = TableView(project)
        val tableContent: Content = contentFactory.createContent(
            tableView.getRootPanel(),
            MessagesBundle.message("view.table.title"), false
        )

        toolWindow.contentManager.addContent(tableContent)

        // Initialize Window Manager
        WindowManager.instance.initialize(toolWindow, treeView, tableView);

        // Синхронизируем UI с данными
        manager.uiBus().addListener(treeView);
        manager.uiBus().addListener(tableView);
        manager.bus().propagate().onUpdateData(manager.store().getData());
    }
}