package com.github.firusv.smarti18n.tabs


import com.github.firusv.smarti18n.InstanceManager
import com.github.firusv.smarti18n.MessagesBundle
import com.github.firusv.smarti18n.dialog.EditDialog
import com.github.firusv.smarti18n.listeners.DeleteKeyListener
import com.github.firusv.smarti18n.listeners.PopupClickListener
import com.github.firusv.smarti18n.listeners.ReturnKeyListener
import com.github.firusv.smarti18n.model.KeyPath
import com.github.firusv.smarti18n.model.Translation
import com.github.firusv.smarti18n.model.TranslationData
import com.github.firusv.smarti18n.model.action.TranslationDelete
import com.github.firusv.smarti18n.model.bus.FilteredBusListener
import com.github.firusv.smarti18n.tabs.mapper.TableModelMapper
import com.github.firusv.smarti18n.tabs.renderer.TableRenderer
import com.github.firusv.smarti18n.util.KeyPathConverter
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import org.jetbrains.annotations.NotNull
import java.awt.*
import java.util.*
import javax.swing.*
/**
 * Отображает состояние переводов в виде таблицы.
 * @author firus-v
 */
class TableView(private val project: Project) : FilteredBusListener {

    private val table: JBTable = JBTable()
    private lateinit var converter: KeyPathConverter
    private lateinit var rootPanel: JPanel
    private lateinit var containerPanel: JPanel

    init {
        table.emptyText.text = MessagesBundle.message("view.empty")
        table.addMouseListener(PopupClickListener { e -> showEditPopup(table.rowAtPoint(e.point)) })
        table.addKeyListener(ReturnKeyListener { showEditPopup(table.selectedRow) })
        table.addKeyListener(DeleteKeyListener { deleteSelectedRows() })
        table.setDefaultRenderer(String::class.java, TableRenderer())

        containerPanel.add(JBScrollPane(table))
    }

    private fun showEditPopup(row: Int) {
        if (row < 0) return

        val fullPath = converter.fromString(table.getValueAt(row, 0).toString())
        val value = InstanceManager.get(project).store().getData().getTranslation(fullPath)

        value?.let {
            EditDialog(project, Translation(fullPath, it)).showAndHandle()
        }
    }

    private fun deleteSelectedRows() {
        val batchDelete = mutableSetOf<KeyPath>()

        for (selectedRow in table.selectedRows) {
            batchDelete.add(converter.fromString(table.getValueAt(selectedRow, 0).toString()))
        }

        batchDelete.forEach { key ->
            InstanceManager.get(project).processUpdate(TranslationDelete(Translation(key, null)))
        }
    }

    override fun onUpdateData(@NotNull data: TranslationData) {
        converter = KeyPathConverter(project)

        table.model = TableModelMapper(data, converter) { update ->
            InstanceManager.get(project).processUpdate(update)
        }
    }

    override fun onFocusKey(@NotNull key: KeyPath) {
        val concatKey = converter.toString(key)
        var row = -1

        for (i in 0 until table.rowCount) {
            if (table.getValueAt(i, 0) == concatKey) {
                row = i
            }
        }

        if (row > -1) { // Ключ найден
            table.selectionModel.setSelectionInterval(row, row)
            table.scrollRectToVisible(Rectangle(table.getCellRect(row, 0, true)))
        }
    }

    override fun onExpandAll() {
        // Представление таблицы никогда не сворачивает строки
    }

    fun getRootPanel(): JPanel {
        return rootPanel
    }

    fun getTable(): JBTable {
        return table
    }
}