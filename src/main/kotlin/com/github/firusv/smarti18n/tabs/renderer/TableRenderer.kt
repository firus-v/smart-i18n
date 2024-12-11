package com.github.firusv.smarti18n.tabs.renderer

import com.intellij.ui.JBColor
import java.awt.Component
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer


/**
 * Аналогично [DefaultTableCellRenderer], но помечает первый столбец красным цветом, если какой-либо столбец пуст.
 * @author firus-v
 */
class TableRenderer : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        var data = value
        if(value == null) {
            data = ""
        }
        val component = super.getTableCellRendererComponent(table, data, isSelected, hasFocus, row, column)

        // Всегда сбрасываем цвет
        component.foreground = null

        if (column != 0) {
            return component
        }

        if (missesValues(row, table)) {
            component.foreground = JBColor.RED
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
                val data = table.getValueAt(row, column);
                if(data === null){
                    return false
                }

                if (contents.contains(data.toString())) {
                    return true
                }
            }
        }

        return false
    }
}