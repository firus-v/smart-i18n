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
import org.jetbrains.annotations.Nullable
import java.io.File

/**
 * Слушает изменения файлов в указанном пути @localesPath.
 * При изменении соответствующего файла вызывается функция перезагрузки экземпляра i18n.
 */
class FileChangeListener(private val project: @NotNull Project) : AsyncFileListener {

    private val logger: Logger = Logger.getInstance(FileChangeListener::class.java)
    private var localesPath: String? = null

    init {
        localesPath = null // Ожидание обновления перед началом прослушивания изменений
    }

    /**
     * Обновляет путь к директории локалей.
     * Если путь корректен и существует, он будет использован для отслеживания изменений.
     */
    fun updateLocalesPath(localesPath: String?) {
        if (!localesPath.isNullOrEmpty()) {
            val file = LocalFileSystem.getInstance().findFileByIoFile(File(localesPath))

            if (file != null && file.isDirectory) {
                this.localesPath = file.path
                return
            }
        }

        this.localesPath = null
    }

    override fun prepareChange(events: List<out VFileEvent>): AsyncFileListener.ChangeApplier {
        return object : AsyncFileListener.ChangeApplier {
            override fun afterVfsChange() {
                localesPath?.let {
                    events.forEach { event ->
                        if (event.path.contains(localesPath!!)) { // Если изменение связано с локалями
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