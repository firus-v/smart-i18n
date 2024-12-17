package com.github.firusv.smarti18n.settings

import com.github.firusv.smarti18n.MessagesBundle
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.observable.util.addListDataListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.TitledSeparator
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
            .addComponent(TitledSeparator(bundle.message("settings.translate.title")))
            .addComponent(getFileListModel())
            .addLabeledComponent(bundle.message("settings.translate.defaultLang.title"), getDefaultLangModel())
            .addLabeledComponent(bundle.message("settings.translate.delimiter"), getDelimiter())
            .addComponent(TitledSeparator(bundle.message("settings.view.title")))
            .addComponent(getViewOptionsGroup())
            .addComponent(TitledSeparator(bundle.message("settings.deepl.title")))
            .addComponent(getDeeplGroup())
            .addComponentFillVertically(JPanel(), 0)
            .panel

        addChangeListeners()
        updateDefaultLang()
    }

    private fun addChangeListeners(){
        showFoldingTranslate.addChangeListener { e ->
            alwaysFoldingTranslate.isEnabled = showFoldingTranslate.isSelected
        }
        deeplEnabled.addChangeListener { _ ->
            deeplApiKey.isEnabled = deeplEnabled.isSelected
        }
        alwaysFoldingTranslate.isEnabled = showFoldingTranslate.isSelected
        deeplApiKey.isEnabled = deeplEnabled.isSelected
    }

    private fun getViewOptionsGroup(): JComponent {
        val form = FormBuilder.createFormBuilder()

        showTableView = JCheckBox()
        showTableView.text = bundle.message("settings.view.showTableView")
        showTreeView = JCheckBox()
        showTreeView.text = bundle.message("settings.view.showTreeView")
        showCurrentFile = JCheckBox()
        showCurrentFile.isEnabled = false
        showCurrentFile.text = bundle.message("settings.view.showCurrentFile")
        showFoldingTranslate = JCheckBox()
        showFoldingTranslate.text = bundle.message("settings.view.showFoldingTranslate")
        alwaysFoldingTranslate = JCheckBox()
        alwaysFoldingTranslate.text = bundle.message("settings.view.alwaysFoldingTranslate")
        showCodeAssistant = JCheckBox()
        showCodeAssistant.text = bundle.message("settings.view.showCodeAssistant")
        isReferenceEnabled = JCheckBox()
        isReferenceEnabled.text = bundle.message("settings.view.isReferenceEnabled")



//        form.addComponent(showTableView)
//        form.addComponent(showTreeView)
        form.addComponent(showCurrentFile)
        form.addComponent(showFoldingTranslate)
        form.addComponent(alwaysFoldingTranslate)
        form.addComponent(showCodeAssistant)
        form.addComponent(isReferenceEnabled)

        return form.panel
    }

    private fun getDeeplGroup(): JComponent {
        val form = FormBuilder.createFormBuilder()

        deeplEnabled = JCheckBox()
        deeplEnabled.text = bundle.message("settings.deepl.deeplEnabled")
        deeplApiKey = JBTextField()

        deeplEnabled.isEnabled = false
        deeplApiKey.isEnabled = false

        form.addComponent(deeplEnabled)
        form.addLabeledComponent(bundle.message("settings.deepl.deeplApiKey"), deeplApiKey)

        form.panel.isEnabled = false
        return form.panel
    }

    fun getFileListModel(): JPanel {
        fileList = JBList(DefaultListModel())
        fileList.cellRenderer = FileListCellRenderer()

        addButton = JButton(bundle.message("settings.translate.add"))
        removeButton = JButton(bundle.message("settings.translate.delete"))

        // отслеживание измененией выбранного элемента в списке
        fileList.addListSelectionListener { event: ListSelectionEvent ->
            // управление активностью кнопки удалить
            checkRemoveButton()
        }

        // Нажатие на кнопку добавления файла
        addButton.addActionListener {
            val fileChooserDescriptor = FileChooserDescriptor(true, false, false, false, false, false)
                .withFileFilter { file -> file.extension == "json" }
                .withTitle(bundle.message("settings.translate.choseJsonFile"))

            val virtualFile: VirtualFile? = FileChooser.chooseFile(fileChooserDescriptor, project, null)

            addFileToListModel(virtualFile)
        }

        // Нажатие на кнопку удаления файла
        removeButton.addActionListener {
            removeSelectedFileFromFileListModel()
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

    fun removeSelectedFileFromFileListModel() {
        val selected = fileList.selectedValue
        if (selected != null) {
            (fileList.model as DefaultListModel<VirtualFile>).removeElement(selected)
        }
        updateDefaultLang()
        checkRemoveButton()
    }

    fun addFileToListModel(virtualFile: VirtualFile?) {
        virtualFile?.let { file ->
            val model = fileList.model as DefaultListModel<VirtualFile>

            // Проверка на уникальность
            if (!model.contains(file)) {
                model.addElement(file)
            }
        }
        updateDefaultLang()
        checkRemoveButton()
    }

    private fun checkRemoveButton() {
        val editable = !fileList.isEmpty && fileList.selectedIndex != -1
        removeButton.isEnabled = editable
    }

    private fun getDefaultLangModel(): JComponent {
        defaultLang = ComboBox<String>()
        defaultLang.model = DefaultComboBoxModel()
        defaultLang.setToolTipText(bundle.message("settings.translate.defaultLang.tooltip"))
        defaultLang.setMinimumAndPreferredWidth(200)

        return defaultLang
    }

    private fun getDelimiter(): JComponent {
        delimiter = JBTextField()
        delimiter.preferredSize = Dimension(72, delimiter.preferredSize.height)
        val panel = JPanel(BorderLayout())
        panel.add(delimiter, BorderLayout.WEST)
        return panel
    }

    fun getMainPanel(): JPanel {
        return mainPanel
    }

}