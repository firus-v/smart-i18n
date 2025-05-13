package com.github.firusv.smarti18n.service

import com.github.firusv.smarti18n.MessagesBundle
import com.intellij.ide.BrowserUtil
import com.intellij.ide.DataManager
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsActions
import com.intellij.util.Consumer
import java.awt.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Отправка отчета об ошибках в репозиторий проекта, если происходят исключения.
 * @author firus-v
 */
class ErrorReportHandler : ErrorReportSubmitter() {

    @NlsActions.ActionText
    override fun getReportActionText(): String {
        return MessagesBundle.message("error.submit")
    }

    override fun submit(
        events: Array<out IdeaLoggingEvent>,
        additionalInfo: String?,
        parentComponent: Component,
        consumer: Consumer<in SubmittedReportInfo>
    ): Boolean {
        if (events.isEmpty()) {
            return false
        }

        val event = events[0]
        val context: DataContext = DataManager.getInstance().getDataContext(parentComponent)
        val project: Project? = CommonDataKeys.PROJECT.getData(context)

        var info = additionalInfo ?: "/"
        val plugin: IdeaPluginDescriptor? = PluginManagerCore.getPlugin(PluginId.getId("com.github.firusv.smarti18n"))
        val version: String = plugin?.version ?: "???"

        val title = "IDE Error Report (v$version)"
        val labels = "ide report"
        val body = """
            # Additional information
            $info
            # Exception trace
            ${event.message}
            ```
            ${event.throwableText}
            ```
        """.trimIndent()

        var url = "https://github.com/firus-v/smart-i18n/issues/new?title=" +
                encodeParam(title) + "&labels=" + encodeParam(labels) + "&body=" + encodeParam(body)

        if (url.length > 8201) { // Учитываем ограничение на длину URL для Github
            url = url.substring(0, 8201)
        }

        val finalUrl = url

        object : Task.Backgroundable(project, "Sending error report") {
            override fun run(indicator: ProgressIndicator) {
                BrowserUtil.browse(finalUrl)
                ApplicationManager.getApplication().invokeLater {
                    consumer.consume(
                        SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE)
                    )
                }
            }
        }.queue()

        return true
    }

    private fun encodeParam(param: String): String {
        return URLEncoder.encode(param, StandardCharsets.UTF_8.toString())
    }
}