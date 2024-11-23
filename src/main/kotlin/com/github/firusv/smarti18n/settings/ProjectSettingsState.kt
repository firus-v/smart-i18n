package com.github.firusv.smarti18n.settings

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import javax.print.DocFlavor
import javax.print.DocFlavor.STRING
import javax.swing.ComboBoxModel
import javax.swing.DefaultComboBoxModel
import javax.swing.DefaultListModel
import javax.swing.ListModel


open class ProjectSettingsState {
    private var fileList: String = ""
    private var defaultLang: String = ""

    fun setFileList(model: ListModel<VirtualFile>) {
        val list = ArrayList<String>();
        for (index in 0 until model.size) {
            list.add(model.getElementAt(index).path)
        }
        fileList = list.joinToString(":fileSeparator:")
    }

    fun setDefaultLang(model: ComboBoxModel<String>) {
        var index = model.selectedItem.toString();
        defaultLang = index
    }

    fun getDefaultLang(fileListModel: ListModel<VirtualFile> = getFileList()): ComboBoxModel<String>{
        val model = DefaultComboBoxModel<String>()

        for(index in 0 until fileListModel.size){
            model.addElement(fileListModel.getElementAt(index).nameWithoutExtension)
        }
        model.selectedItem = defaultLang

        return model;
    }

    fun getFileList(): ListModel<VirtualFile> {
        val model = DefaultListModel<VirtualFile>()
        val list = fileList.split(":fileSeparator:").toTypedArray()
        for (index in 0 until list.size) {
            if(list[index].isEmpty()) continue
            val file = File(list[index])
            if(file.exists()){
                val vf = LocalFileSystem.getInstance().findFileByIoFile(file)
                model.addElement(vf)
            }
        }
        return model
    }

    private fun exists(parent: String, child: String): Boolean {
        return LocalFileSystem.getInstance().findFileByIoFile(File(parent, child)) != null
    }

}