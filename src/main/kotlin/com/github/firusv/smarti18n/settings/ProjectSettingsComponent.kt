package com.github.firusv.smarti18n.settings

import com.github.firusv.smarti18n.MessagesBundle
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.event.ListSelectionEvent

/**
 * Панель конфигурации плагина со всеми возможными опциями.
 *
 * @author firus-v
 */
class ProjectSettingsComponent(private val project: Project) : ProjectSettingsComponentState() {
    private var mainPanel: JPanel
    private var bundle = MessagesBundle

    init {
        mainPanel = FormBuilder.createFormBuilder()
            .addComponent(getFileList())
            .addLabeledComponent(bundle.message("settings.defaultLang.title"), getDefaultLang())
            .addLabeledComponent(bundle.message("settings.delimiter.title"), getDelimiter())
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    private fun getFileList(): JPanel {
        fileList = JBList<VirtualFile>()
        fileList.cellRenderer = FileListCellRenderer()

        addButton = JButton(bundle.message("settings.hint.add"))
        removeButton = JButton(bundle.message("settings.hint.delete"))

        // отслеживание измененией списка
        fileList.addListSelectionListener { event: ListSelectionEvent ->
            // управление активностью кнопки удалить
            if (event.valueIsAdjusting) {
                checkRemoveButton()
            }
        }

        // Нажатие на кнопку добавления файла
        addButton.addActionListener {
            val fileChooserDescriptor = FileChooserDescriptor(true, false, false, false, false, false)
                .withFileFilter { file -> file.extension == "json" }
                .withTitle(bundle.message("settings.hint.choseJsonFile"))

            val virtualFile: VirtualFile? = FileChooser.chooseFile(fileChooserDescriptor, project, null)

            virtualFile?.let { file ->
                val model = fileList.model as DefaultListModel<VirtualFile>

                // Проверка на уникальность
                if (!model.contains(file)) {
                    model.addElement(file)
                }
            }
            updateDefaultLang()
        }

        // Нажатие на кнопку удаления файла
        removeButton.addActionListener {
            val selected = fileList.selectedValue
            if (selected != null) {
                (fileList.model as DefaultListModel<VirtualFile>).removeElement(selected)
            }
            checkRemoveButton()
            updateDefaultLang()
        }

        // Выставления статуса активности для кнопки удаления файла
        checkRemoveButton()

        // Объеденяем кнопки в панель
        val buttonsPanel = JPanel().apply {
            add(addButton)
            add(removeButton)
        }
        // Установка фиксированной высоты для прокручиваемой области списка
        val scrollPane = JBScrollPane(fileList).apply {
            preferredSize = Dimension(preferredSize.width, 170) // Ограничиваем только высоту
        }

        // Добавление в панель списка и кнопок
        return JPanel(BorderLayout()).apply {
            add(scrollPane, BorderLayout.NORTH)
            add(buttonsPanel, BorderLayout.WEST)
        }
    }

    private fun getDefaultLang(): JComponent{
        defaultLang = ComboBox<String>()
        defaultLang.model = DefaultComboBoxModel()
        defaultLang.setToolTipText(bundle.message("settings.defaultLang.tooltip"))
        defaultLang.setMinimumAndPreferredWidth(200)

        return defaultLang
    }

    private fun getDelimiter(): JComponent{
        delimiter = JBTextField()
        delimiter.preferredSize = Dimension(72, delimiter.preferredSize.height)
        val panel = JPanel(BorderLayout())
        panel.add(delimiter, BorderLayout.WEST)
        return panel
    }

    private fun checkRemoveButton(){
        val editable = !fileList.isEmpty && fileList.selectedIndex != -1
        removeButton.isEnabled = editable
    }

    fun getMainPanel(): JPanel {
        return mainPanel
    }

}