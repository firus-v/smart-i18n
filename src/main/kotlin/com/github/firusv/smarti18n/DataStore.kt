package com.github.firusv.smarti18n

import com.github.firusv.smarti18n.exception.EmptyLocalesDirException
import com.github.firusv.smarti18n.io.IOHandler
import com.github.firusv.smarti18n.model.TranslationData
import com.github.firusv.smarti18n.service.FileChangeListener
import com.github.firusv.smarti18n.settings.ProjectSettingsService
import com.github.firusv.smarti18n.util.NotificationHelper
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFileManager


/**
 * Отвечает за загрузку, сохранение и обновление файлов переводов.
 * Обеспечивает доступ к кешированным данным переводов, которые используются во всем проекте.
 * @author firus-v
 */
class DataStore(private val project: Project) {

    private val changeListener: FileChangeListener = FileChangeListener(project)
    private var data: TranslationData = TranslationData(true) // Инициализация с жестко заданной конфигурацией

    init {
        VirtualFileManager.getInstance().addAsyncFileListener(
            changeListener, Disposer.newDisposable(project, "SmartI18n")
        )
    }

    /**
     * Возвращает кешированные данные переводов.
     */
    fun getData(): TranslationData = data

    /**
     * Загружает данные переводов в кеш, перезаписывая любые предыдущие кешированные данные.
     * Если конфигурация не подходит, создается пустой экземпляр переводов.
     * @param successResult Consumer сообщает об успешности операции
     */
    fun loadFromPersistenceLayer(successResult: (Boolean) -> Unit) {
        val settings = ProjectSettingsService.get(project).state
        ApplicationManager.getApplication().runReadAction {
            try {
                data = IOHandler(project, settings).read()
                changeListener.updateLocalesPath(settings.getFileListModel())
                successResult(true)
            } catch (ex: Exception) {
                // TODO Разобраться с сортировкой, возможно стоит сделать через параметр в настройках
                data = TranslationData(false)
                successResult(false)


                if (ex !is EmptyLocalesDirException) {
                    NotificationHelper.createIOError(settings, ex)
                }
            }
        }
    }

    /**
     * Сохраняет кешированные данные переводов в базовую файловую систему.
     * @param successResult Consumer сообщает об успешности операции
     */
    fun saveToPersistenceLayer(successResult: (Boolean) -> Unit) {
        val settings = ProjectSettingsService.get(project).state
        ApplicationManager.getApplication().runWriteAction {
            try {
                IOHandler(project, settings).write(data)
                successResult(true)
            } catch (ex: Exception) {
                successResult(false)
                NotificationHelper.createIOError(settings, ex)
            }
        }
    }
}