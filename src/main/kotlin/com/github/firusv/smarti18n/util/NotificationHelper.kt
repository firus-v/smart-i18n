package com.github.firusv.smarti18n.util

import com.github.firusv.smarti18n.MessagesBundle
import com.github.firusv.smarti18n.exception.SyntaxException
import com.github.firusv.smarti18n.io.IOHandler
import com.github.firusv.smarti18n.settings.ProjectSettingsState
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project

/**
 * Утилита для создания уведомлений с подробной информацией, такой как трассировка исключений.
 * @author firus-v
 */
object NotificationHelper {

    private const val NOTIFICATION_GROUP = "Smart I18n Notification Group"

    fun createIOError(state: ProjectSettingsState, ex: Exception) {
        val bundle = MessagesBundle
        val message = bundle.message("error.io", state.getDefaultLangModel(), state.getDelimiter())
        Logger.getInstance(IOHandler::class.java).error(message, ex)
    }

    fun createBadSyntaxNotification(project: Project, ex: SyntaxException) {

        val notification = Notification(
            NOTIFICATION_GROUP,
            MessagesBundle.message("warning.bad-syntax"),
            ex.message ?: "",
            NotificationType.ERROR
        )

        Notifications.Bus.notify(notification, project)
    }

}
