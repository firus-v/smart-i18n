package com.github.firusv.smarti18n.tabs.mapper

import com.github.firusv.smarti18n.model.KeyPath
import com.github.firusv.smarti18n.model.Translation
import com.github.firusv.smarti18n.model.TranslationData
import com.github.firusv.smarti18n.model.action.TranslationUpdate
import com.github.firusv.smarti18n.util.KeyPathConverter
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel

/**
 * Отображение {@link TranslationData} на {@link TableModel}.
 * @author firus-v
 */
class TableModelMapper(
    private val data: TranslationData,
    private val converter: KeyPathConverter,
    private val updater: (TranslationUpdate) -> Unit
) : TableModel {

    private val locales: List<String> = ArrayList(data.getLocales())
    private val fullKeys: List<KeyPath> = ArrayList(data.fullKeys)

    override fun getRowCount(): Int = fullKeys.size

    override fun getColumnCount(): Int = locales.size + 1 // Количество локалей + 1 (колонка ключей)

    override fun getColumnName(columnIndex: Int): String {
        return if (columnIndex == 0) {
            "<html><b>Key</b></html>"
        } else {
            "<html><b>${locales[columnIndex - 1]}</b></html>"
        }
    }

    override fun getColumnClass(columnIndex: Int): Class<*> = String::class.java

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        val key = fullKeys[rowIndex]

        return if (columnIndex == 0) { // Ключи
            converter.toString(key)
        } else {
            val locale = locales[columnIndex - 1]
            data.getTranslation(key)?.get(locale)
        }
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        val key = fullKeys[rowIndex]
        val translation = data.getTranslation(key) ?: return // Неизвестная ячейка

        val newKey = if (columnIndex == 0) {
            converter.fromString(aValue.toString())
        } else {
            key
        }

        // Обновление содержимого перевода
        if (columnIndex > 0) {
            val locale = locales[columnIndex - 1]
            if (aValue == null || (aValue as String).isEmpty()) {
                translation.remove(locale)
            } else {
                translation.put(locale, aValue.toString())
            }
        }

        val update = TranslationUpdate(
            Translation(key, translation),
            Translation(newKey, translation)
        )

        updater(update)
    }

    override fun addTableModelListener(l: TableModelListener?) {
        // Никаких действий
    }

    override fun removeTableModelListener(l: TableModelListener?) {
        // Никаких действий
    }
}