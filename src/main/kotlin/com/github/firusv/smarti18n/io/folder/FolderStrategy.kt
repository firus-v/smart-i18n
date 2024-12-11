package com.github.firusv.smarti18n.io.folder


import com.github.firusv.smarti18n.io.parser.ParserStrategyType
import com.github.firusv.smarti18n.model.TranslationData
import com.github.firusv.smarti18n.model.TranslationFile
import com.github.firusv.smarti18n.settings.ProjectSettingsState
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NotNull
import java.io.File
import java.io.IOException
import java.util.*
import javax.swing.ListModel


/**
 * Представляет структуру каталога для конкретного файла перевода.
 * @author firus-v
 */
class FolderStrategy() {

    /**
     * Принимает модель с файлами перевода.
     * @param fileListModel модель со списком файлов перевода
     * @return файлы перевода, соответствующие стратегии
     */
    fun analyzeFileList(fileListModel: ListModel<VirtualFile>): List<TranslationFile>{
        val list: MutableList<TranslationFile> = ArrayList<TranslationFile>();
        for(index in 0 until fileListModel.size){
            val file = fileListModel.getElementAt(index)!!;
            list.add(TranslationFile(file, file.nameWithoutExtension, null))
        }
        return list
    }

    /**
     * Проверяет, существует ли указанный файл или каталог.
     * @param parent Родительский путь
     * @param child Имя файла / каталога
     * @return true, если файл существует, иначе false
     */
    protected fun exists(parent: String, child: String): Boolean {
        return LocalFileSystem.getInstance().findFileByIoFile(File(parent, child)) != null
    }
}