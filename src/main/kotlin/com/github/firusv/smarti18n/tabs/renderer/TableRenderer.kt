package com.github.firusv.smarti18n.tabs.renderer

import com.intellij.ui.JBColor
import java.awt.Component
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer


/**
 * Similar to [DefaultTableCellRenderer] but will mark the first column red if any column is empty.
 * @author marhali
 */
class TableRenderer : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        val component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)

        // Always reset color
        component.foreground = null

        if (column != 0) {
            return component
        }

        if (missesValues(row, table)) {
            component.foreground = JBColor.RED
        } else if (hasDuplicates(row, table)) {
            component.foreground = JBColor.ORANGE
        }

        return component
    }

    private fun missesValues(row: Int, table: JTable): Boolean {
        val columns = table.columnCount

        for (i in 1 until columns) {
            val value = table.getValueAt(row, i)

            if (value == null || value.toString().isEmpty()) {
                return true
            }
        }

        return false
    }

    private fun hasDuplicates(checkRow: Int, table: JTable): Boolean {
        val columns = table.columnCount
        val rows = table.rowCount

        val contents: MutableSet<String> = HashSet()
        for (column in 1 until columns) {
            contents.add(table.getValueAt(checkRow, column).toString())
        }

        for (row in 1 until rows) {
            if (row == checkRow) {
                continue
            }

            for (column in 1 until columns) {
                if (contents.contains(table.getValueAt(row, column).toString())) {
                    return true
                }
            }
        }

        return false
    }
}