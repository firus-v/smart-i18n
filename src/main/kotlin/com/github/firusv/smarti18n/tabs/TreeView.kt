package com.github.firusv.smarti18n.tabs

import com.github.firusv.smarti18n.InstanceManager
import com.github.firusv.smarti18n.MessagesBundle
import com.github.firusv.smarti18n.action.treeview.CollapseTreeViewAction
import com.github.firusv.smarti18n.action.treeview.ExpandTreeViewAction
import com.github.firusv.smarti18n.listeners.DeleteKeyListener
import com.github.firusv.smarti18n.listeners.PopupClickListener
import com.github.firusv.smarti18n.listeners.ReturnKeyListener
import com.github.firusv.smarti18n.model.KeyPath
import com.github.firusv.smarti18n.model.Translation
import com.github.firusv.smarti18n.model.TranslationData
import com.github.firusv.smarti18n.model.action.TranslationDelete
import com.github.firusv.smarti18n.model.bus.FilteredBusListener
import com.github.firusv.smarti18n.settings.ProjectSettingsService
import com.github.firusv.smarti18n.tabs.mapper.TreeModelMapper
import com.github.firusv.smarti18n.tabs.renderer.TreeRenderer
import com.github.firusv.smarti18n.util.TreeUtil
import com.intellij.ide.projectView.PresentationData
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

/**
 * Отображает состояние перевода в виде дерева.
 * @author firus-v
 */
class TreeView(private val project: Project) : FilteredBusListener {

    private val tree = Tree()
    private var currentMapper: TreeModelMapper? = null

    private lateinit var rootPanel: JPanel
    private lateinit var toolBarPanel: JPanel
    private lateinit var containerPanel: JPanel

    init {
        tree.cellRenderer = TreeRenderer()
        tree.isRootVisible = false
        tree.emptyText.text = MessagesBundle.message("view.empty")
        tree.addMouseListener(PopupClickListener { e -> showEditPopup(tree.getPathForLocation(e.x, e.y)) })
        tree.addKeyListener(ReturnKeyListener { showEditPopup(tree.selectionPath) })
        tree.addKeyListener(DeleteKeyListener { deleteSelectedNodes() })

        containerPanel.add(JBScrollPane(tree))
        placeActions()
    }

    private fun placeActions() {
        val group = DefaultActionGroup("TranslationsGroup", false)

        val expand = ExpandTreeViewAction { onExpandAll() }
        val collapse = CollapseTreeViewAction { onCollapseAll() }

        group.add(collapse)
        group.add(expand)

        val actionToolbar = ActionManager.getInstance()
            .createActionToolbar("TranslationsActions", group, false)

        actionToolbar.targetComponent = toolBarPanel
        toolBarPanel.add(actionToolbar.component)
    }

    override fun onUpdateData(@NotNull data: TranslationData) {
        val expanded = getExpandedRows()
        tree.model = TreeModelMapper(data, ProjectSettingsService.get(project).state).also {
            currentMapper = it
        }
        expanded.forEach { tree.expandRow(it) }
    }

    private fun getExpandedRows(): List<Int> {
        return (0 until tree.rowCount).filter { tree.isExpanded(it) }
    }

    override fun onFocusKey(@NotNull key: KeyPath) {
        currentMapper?.let { mapper ->
            val path = mapper.findTreePath(key)
            tree.selectionModel.setSelectionPath(path)
            tree.scrollPathToVisible(path)

            if (tree.isCollapsed(path)) {
                tree.expandPath(path)
            }
        }
    }

    private fun showEditPopup(@Nullable path: TreePath?) {
        path ?: return
        val node = path.lastPathComponent as? DefaultMutableTreeNode ?: return

        if (node.userObject !is PresentationData) return

        val fullPath = TreeUtil.getFullPath(path)
        InstanceManager.get(project).store().getData().getTranslation(fullPath) ?: return

        // TODO добавить редактирование перевода для TreeView
//        EditDialog(project, Translation(fullPath, value)).showAndHandle()
    }

    private fun deleteSelectedNodes() {
        val selection = tree.selectionPaths ?: return
        val batchDelete = selection.mapTo(mutableSetOf()) { TreeUtil.getFullPath(it) }

        batchDelete.forEach { key ->
            InstanceManager.get(project).processUpdate(TranslationDelete(Translation(key, null)))
        }
    }

    override fun onExpandAll() {
        for (i in 0 until tree.rowCount) {
            tree.expandRow(i)
        }
    }

    fun onCollapseAll() {
        for (i in 0 until tree.rowCount) {
            tree.collapseRow(i)
        }
    }

    fun getRootPanel(): JPanel = rootPanel

    fun getTree(): Tree = tree
}