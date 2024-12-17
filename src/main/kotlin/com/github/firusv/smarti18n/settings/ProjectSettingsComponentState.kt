package com.github.firusv.smarti18n.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import javax.swing.JButton
import javax.swing.JCheckBox

// Отвечает за хранение состояния компоненты.
open class ProjectSettingsComponentState {
    // Окно выбора файлов перевода
    lateinit var fileList: JBList<VirtualFile>

    // Выпаающий список языка по умолчанию
    lateinit var defaultLang: ComboBox<String>

    // Разделитель
    lateinit var delimiter: JBTextField

    // Показывать вкладку "Table View"
    lateinit var showTableView: JCheckBox

    // Показывать вкладку "Tree View"
    lateinit var showTreeView: JCheckBox

    // Показывать вкладку "Current File" (переводы текущего файла)
    lateinit var showCurrentFile: JCheckBox

    // Показывать перевод в коде?
    lateinit var showFoldingTranslate: JCheckBox

    // Всегда скрывать ключ и показывать перевод
    lateinit var alwaysFoldingTranslate: JCheckBox

    // Автодополнение ключей перевода
    lateinit var showCodeAssistant: JCheckBox

    // Переход по ссылкам
    lateinit var isReferenceEnabled: JCheckBox

    // Включить автодополнение перевода через Deepl.com
    lateinit var deeplEnabled: JCheckBox

    // Поле ввода API ключа для Deepl.com
    lateinit var deeplApiKey: JBTextField

    lateinit var addButton: JButton
    lateinit var removeButton: JButton

    // state -> поля
    fun setState(state: ProjectSettingsState) {
        fileList.model = state.getFileListModel()
        defaultLang.model = state.getDefaultLangModel()
        delimiter.text = state.getDelimiter()

        showTableView.isSelected = state.getShowTableView()
        showTreeView.isSelected = state.getShowTreeView()
        showCurrentFile.isSelected = state.getShowCurrentFile()
        showFoldingTranslate.isSelected = state.getShowFoldingTranslate()
        alwaysFoldingTranslate.isSelected = state.getAlwaysFoldingTranslate()
        showCodeAssistant.isSelected = state.getShowCodeAssistant()
        isReferenceEnabled.isSelected = state.getIsReferenceEnabled()

        deeplEnabled.isSelected = state.getDeeplEnabled()
        deeplApiKey.text = state.getDeeplApiKey()

        updateDefaultLang()

    }

    // поля -> state
    fun getState(): ProjectSettingsState {
        val state = ProjectSettingsState()

        state.setFileListModel(fileList.model)
        state.setDefaultLangModel(defaultLang.model)
        state.setDelimiter(delimiter.text)

        state.setShowTableView(showTableView.isSelected)
        state.setShowTreeView(showTreeView.isSelected)
        state.setShowCurrentFile(showCurrentFile.isSelected)
        state.setShowFoldingTranslate(showFoldingTranslate.isSelected)
        state.setAlwaysFoldingTranslate(alwaysFoldingTranslate.isSelected)
        state.setShowCodeAssistant(showCodeAssistant.isSelected)
        state.setIsReferenceEnabled(isReferenceEnabled.isSelected)

        state.setDeeplEnabled(deeplEnabled.isSelected)
        state.setDeeplApiKey(deeplApiKey.text)

        return state
    }

    fun updateDefaultLang() {
        val state = ProjectSettingsState()
        defaultLang.model = state.getDefaultLangModel(fileList.model)
        if (defaultLang.model.size > 1) {
            for (index in 0 until defaultLang.model.size) {
                if (defaultLang.model.getElementAt(index) == "") {
                    defaultLang.remove(index)
                    break
                }
            }
        }
        defaultLang.isEnabled = defaultLang.model.size > 0
    }
}