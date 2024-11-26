package com.github.firusv.smarti18n.tabs

import com.github.firusv.smarti18n.model.bus.FilteredBusListener
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;


import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;


class TableView(project: Project) : FilteredBusListener {
    val table: JBTable = JBTable()

    private val project: Project = project

    private var converter: KeyPathConverter? = null

    val rootPanel: JPanel? = null
    private val containerPanel: JPanel? = null

    init {
        table.emptyText.setText(ResourceBundle.getBundle("messages").getString("view.empty"))
        table.addMouseListener(PopupClickListener { e -> showEditPopup(table.rowAtPoint(e.getPoint())) })
        table.addKeyListener(ReturnKeyListener { showEditPopup(table.selectedRow) })
        table.addKeyListener(DeleteKeyListener { this.deleteSelectedRows() })
        table.setDefaultRenderer(String::class.java, TableRenderer())

        containerPanel!!.add(JBScrollPane(table))
    }

    private fun showEditPopup(row: Int) {
        if (row < 0) {
            return
        }

        val fullPath: KeyPath = converter.fromString(table.getValueAt(row, 0).toString())
        val value: TranslationValue = InstanceManager.get(project).store().getData().getTranslation(fullPath)

        if (value != null) {
            EditDialog(project, Translation(fullPath, value)).showAndHandle()
        }
    }

    private fun deleteSelectedRows() {
        val batchDelete: MutableSet<KeyPath> = HashSet()

        for (selectedRow in table.selectedRows) {
            batchDelete.add(converter.fromString(table.getValueAt(selectedRow, 0).toString()))
        }

        for (key in batchDelete) {
            InstanceManager.get(project).processUpdate(TranslationDelete(Translation(key, null)))
        }
    }

    override fun onUpdateData(@NotNull data: TranslationData?) {
        this.converter = KeyPathConverter(project)

        table.setModel(TableModelMapper(data, this.converter) { update ->
            InstanceManager.get(project).processUpdate(update)
        })
    }

    override fun onFocusKey(@NotNull key: KeyPath?) {
        val concatKey: String = converter.toString(key)
        var row = -1

        for (i in 0 until table.rowCount) {
            if (table.getValueAt(i, 0) == concatKey) {
                row = i
            }
        }

        if (row > -1) { // Matched @key
            table.selectionModel.setSelectionInterval(row, row)
            table.scrollRectToVisible(Rectangle(table.getCellRect(row, 0, true)))
        }
    }

    override fun onExpandAll() {
        // table-view never collapse any rows
    }
}