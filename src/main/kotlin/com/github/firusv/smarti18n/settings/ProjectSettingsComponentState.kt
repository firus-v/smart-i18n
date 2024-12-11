package com.github.firusv.smarti18n.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import javax.swing.JButton

// Отвечает за хранение состояния компоненты.
open class ProjectSettingsComponentState {
    lateinit var fileList: JBList<VirtualFile>
    lateinit var defaultLang: ComboBox<String>
    lateinit var delimiter: JBTextField

    lateinit var addButton: JButton
    lateinit var removeButton: JButton

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
        if(defaultLang.model.size > 1){
            for (index in 0  until defaultLang.model.size){
                if(defaultLang.model.getElementAt(index) == ""){
                    defaultLang.remove(index)
                    break
                }
            }
        }
    }
}