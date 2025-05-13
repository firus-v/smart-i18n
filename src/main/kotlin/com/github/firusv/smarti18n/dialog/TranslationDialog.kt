package com.github.firusv.smarti18n.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.util.Consumer
import com.intellij.util.ui.FormBuilder
import com.github.firusv.smarti18n.InstanceManager
import com.github.firusv.smarti18n.MessagesBundle
import com.github.firusv.smarti18n.model.Translation
import com.github.firusv.smarti18n.model.TranslationValue
import com.github.firusv.smarti18n.model.action.TranslationUpdate
import com.github.firusv.smarti18n.settings.ProjectSettingsState
import com.github.firusv.smarti18n.settings.ProjectSettingsService
import com.github.firusv.smarti18n.util.KeyPathConverter
import javax.swing.*
import javax.swing.border.EtchedBorder
import java.awt.*

/**
 * Базовый класс для диалогов добавления и редактирования переводов.
 * @author firus-v
 */
abstract class TranslationDialog(
    protected val project: Project, // Открытый проект
    protected val origin: Translation // Предустановленный перевод
) : DialogWrapper(project) {

    protected val settings: ProjectSettingsState = ProjectSettingsService.get(project).state
    protected val converter: KeyPathConverter = KeyPathConverter(settings)
    protected val translateKeyField: JTextField = JBTextField(converter.toString(origin.key))
    protected val localeValueFields: MutableMap<String, JTextField> = mutableMapOf()

    private val callbacks: MutableSet<Consumer<TranslationUpdate>> = mutableSetOf()

    init {
        val value = origin.getValue()
        for (locale in InstanceManager.get(project).store().getData().getLocales()) {
            localeValueFields[locale] = JBTextField(value?.get(locale))
        }
    }

    fun getKeyField(): JTextField = translateKeyField

    /**
     * Регистрирует callback, который вызывается при закрытии диалога с финальным состоянием.
     * Если пользователь отменяет диалог, callback не вызывается.
     * @param callback Callback для регистрации
     */
    fun registerCallback(callback: Consumer<TranslationUpdate>) {
        callbacks.add(callback)
    }

    /**
     * Реализация должна обработать выход.
     * @param exitCode См. {@link com.intellij.openapi.ui.DialogWrapper} для кодов выхода
     * @return результат обновления, null если отменено
     */
    protected abstract fun handleExit(exitCode: Int): TranslationUpdate?

    /**
     * Открывает модальное окно перевода и применяет соответствующую логику при закрытии.
     * Внутренне метод {@link #handleExit(int)} будет вызван для определения логики завершения.
     */
    fun showAndHandle() {
        init()
        show()

        val exitCode = exitCode
        val update = handleExit(exitCode)

        if (update != null) {
            InstanceManager.get(project).processUpdate(update)
            callbacks.forEach { it.consume(update) }
        }
    }

    /**
     * Получает текущее состояние модального окна.
     * @return Translation
     */
    protected fun getState(): Translation {
        val key = converter.fromString(translateKeyField.text)
        val value = TranslationValue()

        for ((locale, field) in localeValueFields) {
            value.put(locale, field.text)
        }

        return Translation(key, value)
    }

    override fun createCenterPanel(): JComponent? {
        val panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(MessagesBundle.message("translation.key"), translateKeyField, true)
            .addComponent(createLocalesPanel(), 12)
            .panel

        panel.minimumSize = Dimension(200, 150)
        return panel
    }

    private fun createLocalesPanel(): JComponent {
        val builder = FormBuilder.createFormBuilder()

        for ((locale, field) in localeValueFields) {
            builder.addLabeledComponent(locale, field, 6, true)
        }

        val scrollPane = JBScrollPane(builder.panel)
        scrollPane.border = BorderFactory.createTitledBorder(
            EtchedBorder(), MessagesBundle.message("translation.locales")
        )

        return scrollPane
    }

}
