package com.github.firusv.smarti18n.settings

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import javax.swing.ComboBoxModel
import javax.swing.DefaultComboBoxModel
import javax.swing.DefaultListModel
import javax.swing.ListModel


open class ProjectSettingsState {
    private var fileList: String = ""
    private var defaultLang: String = ""
    private var delimiter: String = "."

    private var showTableView: Boolean = true
    private var showTreeView: Boolean = true
    private var showCurrentFile: Boolean = false
    private var showFoldingTranslate: Boolean = true
    private var alwaysFoldingTranslate: Boolean = false
    private var showCodeAssistant: Boolean = true

    private var deeplEnabled: Boolean = false
    private var deeplApiKey: String = ""


    fun getFileList(): String {
        return fileList
    }

    fun setFileList(fileList: String) {
        this.fileList = fileList
    }

    fun getDefaultLang(): String {
        return defaultLang
    }

    fun setDefaultLang(defaultLang: String) {
        this.defaultLang = defaultLang
    }

    fun setFileListModel(model: ListModel<VirtualFile>) {
        val list = ArrayList<String>()
        for (index in 0 until model.size) {
            list.add(model.getElementAt(index).path)
        }
        fileList = list.joinToString(":fileSeparator:")
    }

    fun setDefaultLangModel(model: ComboBoxModel<String>) {
        var index = ""
        if(model.selectedItem != null) {
            index = model.selectedItem.toString()
        }
        defaultLang = index
    }

    fun setDelimiter(value: String) {
        delimiter = value
    }

    fun getDefaultLangModel(fileListModel: ListModel<VirtualFile> = getFileListModel()): ComboBoxModel<String> {
        val model = DefaultComboBoxModel<String>()

        for (index in 0 until fileListModel.size) {
            model.addElement(fileListModel.getElementAt(index).nameWithoutExtension)
        }
        if (defaultLang != "") {
            model.selectedItem = defaultLang
        }

        return model
    }

    fun getDefaultLangModelToString(): String{
        return defaultLang
    }

    fun getFileListModel(): ListModel<VirtualFile> {
        val model = DefaultListModel<VirtualFile>()
        val list = fileList.split(":fileSeparator:").toTypedArray()
        for (index in list.indices) {
            if (list[index].isEmpty()) continue
            val file = File(list[index])
            if (file.exists()) {
                val vf = LocalFileSystem.getInstance().findFileByIoFile(file)
                model.addElement(vf)
            }
        }
        return model
    }

    fun getDelimiter(): String {
        return delimiter
    }

    fun getShowTableView(): Boolean {
        return showTableView
    }

    fun getShowTreeView(): Boolean {
        return showTreeView
    }

    fun getShowCurrentFile(): Boolean {
        return showCurrentFile
    }

    fun getShowFoldingTranslate(): Boolean {
        return showFoldingTranslate
    }

    fun getAlwaysFoldingTranslate(): Boolean {
        return alwaysFoldingTranslate
    }

    fun getShowCodeAssistant(): Boolean {
        return showCodeAssistant
    }

    fun setShowTableView(v: Boolean) {
        showTableView = v
    }

    fun setShowTreeView(v: Boolean) {
        showTreeView = v
    }

    fun setShowCurrentFile(v: Boolean) {
        showCurrentFile = v
    }

    fun setShowFoldingTranslate(v: Boolean) {
        showFoldingTranslate = v
    }

    fun setAlwaysFoldingTranslate(v: Boolean) {
        alwaysFoldingTranslate = v
    }

    fun setShowCodeAssistant(v: Boolean) {
        showCodeAssistant = v
    }

    fun getDeeplEnabled(): Boolean {
        return deeplEnabled
    }

    fun setDeeplEnabled(v: Boolean) {
        deeplEnabled = v
    }

    fun getDeeplApiKey(): String {
        return deeplApiKey
    }

    fun setDeeplApiKey(v: String) {
        deeplApiKey = v
    }


    private fun exists(parent: String, child: String): Boolean {
        return LocalFileSystem.getInstance().findFileByIoFile(File(parent, child)) != null
    }

}