package com.github.firusv.smarti18n.dialog

import com.github.firusv.smarti18n.MessagesBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.github.firusv.smarti18n.model.action.TranslationCreate
import com.github.firusv.smarti18n.model.KeyPath
import com.github.firusv.smarti18n.model.Translation
import com.github.firusv.smarti18n.model.TranslationValue
import com.github.firusv.smarti18n.model.action.TranslationUpdate
import com.github.firusv.smarti18n.settings.ProjectSettingsService
import java.util.function.Consumer

/**
 * Диалог для создания нового перевода со всеми соответствующими значениями локалей.
 * Поддерживает опциональную технику предварительного заполнения для ключа перевода или значения локали.
 * @author firus-v
 */
class AddDialog(
    project: Project,
    prefillKey: KeyPath? = null,
    prefillLocale: String? = null,
    private val onCreated: Consumer<String>? = null
) : TranslationDialog(
    project,
    Translation(
        prefillKey ?: KeyPath(),
        prefillLocale?.let {
            TranslationValue(
                ProjectSettingsService.get(project).state.getDefaultLangToString(),
                prefillLocale
            )
        }
    )
) {

    init {
        title = MessagesBundle.message("action.add")
        translateKeyField.text = ""
    }

    /**
     * Конструктор без предварительно заполненных полей.
     * @param project Открытый проект
     */
    constructor(project: Project) : this(project, KeyPath(), "")

    override fun handleExit(exitCode: Int): TranslationUpdate? {
        if (exitCode == DialogWrapper.OK_EXIT_CODE) {
            onCreated?.accept(this.getKeyField().text)
            return TranslationCreate(getState())
        }
        return null
    }
}
