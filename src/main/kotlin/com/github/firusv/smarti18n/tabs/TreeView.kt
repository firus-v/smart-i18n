package com.github.firusv.smarti18n.tabs

import com.github.firusv.smarti18n.model.bus.FilteredBusListener
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.*;



class TreeView(project: Project) : FilteredBusListener {
    private val tree: Tree

    private val project: Project = project

    private var currentMapper: TreeModelMapper? = null

    val rootPanel: JPanel? = null
    private val toolBarPanel: JPanel? = null
    private val containerPanel: JPanel? = null

    init {
        tree = Tree()
        tree.setCellRenderer(TreeRenderer())
        tree.setRootVisible(false)
        tree.getEmptyText().setText(ResourceBundle.getBundle("messages").getString("view.empty"))
        tree.addMouseListener(PopupClickListener { e -> showEditPopup(tree.getPathForLocation(e.getX(), e.getY())) })
        tree.addKeyListener(ReturnKeyListener { showEditPopup(tree.getSelectionPath()) })
        tree.addKeyListener(DeleteKeyListener { this.deleteSelectedNodes() })

        containerPanel!!.add(JBScrollPane(tree))
        placeActions()
    }

    private fun placeActions() {
        val group = DefaultActionGroup("TranslationsGroup", false)

        val expand: ExpandTreeViewAction = ExpandTreeViewAction { this.onExpandAll() }
        val collapse: CollapseTreeViewAction = CollapseTreeViewAction { this.onCollapseAll() }

        group.add(collapse)
        group.add(expand)

        val actionToolbar: ActionToolbar = ActionManager.getInstance()
            .createActionToolbar("TranslationsActions", group, false)

        actionToolbar.targetComponent = toolBarPanel
        toolBarPanel!!.add(actionToolbar.component)
    }

    override fun onUpdateData(@NotNull data: TranslationData?) {
        val expanded = expandedRows
        tree.setModel(TreeModelMapper(data, ProjectSettingsService.get(project).getState()).also {
            this.currentMapper = it
        })
        expanded.forEach(tree::expandRow)
    }

    private val expandedRows: List<Int>
        get() {
            val expanded: MutableList<Int> = ArrayList()

            for (i in 0 until tree.getRowCount()) {
                if (tree.isExpanded(i)) {
                    expanded.add(i)
                }
            }

            return expanded
        }

    override fun onFocusKey(@NotNull key: KeyPath?) {
        if (currentMapper != null) {
            val path: TreePath = currentMapper.findTreePath(key)

            tree.getSelectionModel().setSelectionPath(path)
            tree.scrollPathToVisible(path)

            if (tree.isCollapsed(path)) {
                tree.expandPath(path)
            }
        }
    }

    private fun showEditPopup(@Nullable path: TreePath?) {
        if (path == null) {
            return
        }

        val node = path.getLastPathComponent() as DefaultMutableTreeNode

        if (node.userObject !is PresentationData) {
            return
        }

        val fullPath: KeyPath = TreeUtil.getFullPath(path)
        val value: TranslationValue = InstanceManager.get(project).store().getData().getTranslation(fullPath) ?: return

        EditDialog(project, Translation(fullPath, value)).showAndHandle()
    }

    private fun deleteSelectedNodes() {
        val selection: Array<TreePath> = tree.getSelectionPaths()
        val batchDelete: MutableSet<KeyPath> = HashSet()

        if (selection == null) {
            return
        }

        for (path in selection) {
            batchDelete.add(TreeUtil.getFullPath(path))
        }

        for (key in batchDelete) {
            InstanceManager.get(project).processUpdate(TranslationDelete(Translation(key, null)))
        }
    }

    override fun onExpandAll() {
        for (i in 0 until tree.getRowCount()) {
            tree.expandRow(i)
        }
    }

    fun onCollapseAll() {
        for (i in 0 until tree.getRowCount()) {
            tree.collapseRow(i)
        }
    }

    fun getTree(): Tree {
        return tree
    }
}