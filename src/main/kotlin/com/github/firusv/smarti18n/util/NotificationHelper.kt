package com.github.firusv.smarti18n.util

import com.github.firusv.smarti18n.MessagesBundle
import com.github.firusv.smarti18n.exception.SyntaxException
import com.github.firusv.smarti18n.settings.ProjectSettingsState
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.text.MessageFormat
import java.util.*

/**
 * Утилита для создания уведомлений с подробной информацией, такой как трассировка исключений.
 * @author firus-v
 */
object NotificationHelper {

    private const val NOTIFICATION_GROUP = "Easy I18n Notification Group"

        // TODO Доработать сервис уведомлений

//    fun createIOError(state: ProjectSettingsState, ex: Exception) {
//        val bundle = ResourceBundle.getBundle("messages")
//
//        val message = MessageFormat.format(
//            bundle.getString("error.io"),
//            state.folderStrategy, state.parserStrategy, state.filePattern, state.localesDirectory
//        )
//
//        Logger.getInstance(IOHandler::class.java).error(message, ex)
//    }

    fun createBadSyntaxNotification(project: Project, ex: SyntaxException) {

        val notification = Notification(
            NOTIFICATION_GROUP,
            MessagesBundle.message("warning.bad-syntax"),
            ex.message ?: "",
            NotificationType.ERROR
        )

//        notification.addAction(OpenFileAction(ex.file.virtualFile, false))
//        notification.addAction(SettingsAction(false))

        Notifications.Bus.notify(notification, project)
    }

//    fun createEmptyLocalesDirNotification(project: Project) {
//        val bundle = ResourceBundle.getBundle("messages")
//
//        val notification = Notification(
//            NOTIFICATION_GROUP,
//            "Easy I18n",
//            bundle.getString("warning.missing-config"),
//            NotificationType.WARNING
//        )
//
//        notification.addAction(SettingsAction())
//
//        Notifications.Bus.notify(notification, project)
//    }
}
