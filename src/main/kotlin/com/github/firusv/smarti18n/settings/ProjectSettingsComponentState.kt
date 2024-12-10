package com.github.firusv.smarti18n.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import javax.swing.JButton

// Отвечает за хранение состояния компоненты.
open class ProjectSettingsComponentState {
    protected lateinit var fileList: JBList<VirtualFile>
    protected lateinit var defaultLang: ComboBox<String>
    protected lateinit var delimiter: JBTextField

    protected lateinit var addButton: JButton
    protected lateinit var removeButton: JButton

    // state -> поля
    fun setState(state: ProjectSettingsState) {
        fileList.model = state.getFileList()
        defaultLang.model = state.getDefaultLang()
        delimiter.text = state.getDelimiter()
    }

    // поля -> state
    fun getState(): ProjectSettingsState {
        val state = ProjectSettingsState()

        state.setFileList(fileList.model)
        state.setDefaultLang(defaultLang.model)
        state.setDelimiter(delimiter.text)

        return state
    }

    fun updateDefaultLang(){
        val state = ProjectSettingsState()
        defaultLang.model = state.getDefaultLang(fileList.model)
    }
}