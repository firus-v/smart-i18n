package com.github.firusv.smarti18n.service

import com.github.firusv.smarti18n.InstanceManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import org.jetbrains.annotations.NotNull
import java.io.File
import java.util.ArrayList
import javax.swing.ListModel

/**
 * Слушает изменения файлов в указанном пути @localesPath.
 * При изменении соответствующего файла вызывается функция перезагрузки экземпляра i18n.
 */
class FileChangeListener(private val project: @NotNull Project) : AsyncFileListener {

    private val logger: Logger = Logger.getInstance(FileChangeListener::class.java)
    private var fileList: MutableList<String> = ArrayList<String>();

    /**
     * Обновляет путь к директории локалей.
     * Если путь корректен и существует, он будет использован для отслеживания изменений.
     */
    fun updateLocalesPath(fileListModel: ListModel<VirtualFile>) {
        if (fileListModel.size > 0) {
            val list: MutableList<String> = ArrayList<String>();
            for(index in 0 until fileListModel.size){
                val vf = fileListModel.getElementAt(index)
                val file = LocalFileSystem.getInstance().findFileByIoFile(File(vf.path))

                if (file != null) {
                    list.add(file.path)
                }
            }
            this.fileList = list
            return
        }

        this.fileList = ArrayList<String>();
    }

    override fun prepareChange(events: List<out VFileEvent>): AsyncFileListener.ChangeApplier {
        return object : AsyncFileListener.ChangeApplier {
            override fun afterVfsChange() {
                if(fileList.size > 0){
                    events.forEach { event ->
                        fileList.forEach { file ->
                            if(event.path.contains(file)){
                                logger.debug("Изменение файла обнаружено. Перезагружаем экземпляр...")
                                ApplicationManager.getApplication().invokeLater {
                                    InstanceManager.get(project).reload()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}