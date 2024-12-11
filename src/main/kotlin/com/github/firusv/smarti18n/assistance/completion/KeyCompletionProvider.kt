package com.github.firusv.smarti18n.assistance.completion

import com.github.firusv.smarti18n.InstanceManager
import com.github.firusv.smarti18n.model.Translation
import com.github.firusv.smarti18n.settings.ProjectSettingsService
import com.github.firusv.smarti18n.settings.ProjectSettingsState
import com.github.firusv.smarti18n.util.KeyPathConverter
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader.getIcon
import com.intellij.util.ProcessingContext
import org.jetbrains.annotations.NotNull


/**
 * Предоставляет существующие ключи перевода для автодополнения кода.
 * @author firus-v
 */
internal class KeyCompletionProvider : CompletionProvider<CompletionParameters>(),
    OptionalAssistance {
    override fun addCompletions(
        @NotNull parameters: CompletionParameters,
        @NotNull context: ProcessingContext, @NotNull result: CompletionResultSet
    ) {
        val project: Project = parameters.originalFile.project

        if (!isAssistance(project)) {
            return
        }

        val settings: ProjectSettingsState = ProjectSettingsService.get(project).state
        val data = InstanceManager.get(project).store().getData()
        val fullKeys = data.fullKeys

        for (key in fullKeys) {
            result.addElement(constructLookup(Translation(key, data.getTranslation(key)), settings))
        }
    }

    private fun constructLookup(translation: Translation, settings: ProjectSettingsState): LookupElement {
        val converter = KeyPathConverter(settings)
        val defaultLang = settings.getDefaultLang()

        return LookupElementBuilder
            .create(converter.toString(translation.key))
            .withTailText(" " + translation.getValue()!!.get(defaultLang.selectedItem.toString()), true)
            .withIcon(icon)
    }

    companion object {
        private val icon = getIcon("/smart-icon.svg", KeyCompletionProvider::class.java)
    }
}