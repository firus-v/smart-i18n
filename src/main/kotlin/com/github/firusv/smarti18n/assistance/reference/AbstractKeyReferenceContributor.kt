package com.github.firusv.smarti18n.assistance.reference

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.github.firusv.smarti18n.InstanceManager
import com.github.firusv.smarti18n.assistance.OptionalAssistance
import com.github.firusv.smarti18n.model.KeyPath
import com.github.firusv.smarti18n.model.Translation
import com.github.firusv.smarti18n.model.TranslationValue
import com.github.firusv.smarti18n.settings.ProjectSettingsState
import com.github.firusv.smarti18n.settings.ProjectSettingsService
import com.github.firusv.smarti18n.util.KeyPathConverter
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * Абстрактный обработчик ссылок для ключей перевода.
 * @author firus-v
 */
abstract class AbstractKeyReferenceContributor : PsiReferenceContributor(), OptionalAssistance {

    /**
     * Поиск ссылок на релевантные ключи перевода.
     * @param project Открытый проект
     * @param element Целевой элемент
     * @param text Указанный ключ перевода
     * @return Найденные ссылки на ключи перевода
     */
    protected fun getReferences(
        @NotNull project: Project,
        @NotNull element: PsiElement,
        @Nullable text: String?
    ): @NotNull Array<PsiReference> {
        if (text.isNullOrEmpty() || !isReference(project)) {
            return PsiReference.EMPTY_ARRAY
        }

        val settings: ProjectSettingsState = ProjectSettingsService.get(project).state
        val converter = KeyPathConverter(settings)

        // TODO: Нужно предоставлять несколько ссылок, если указана не конечная ветка (поддержка контекста/множественных чисел)

        val delimiter = settings.getDelimiter()

        val regex = """^([a-zA-Z_][a-zA-Z0-9_]*)(${delimiter}[a-zA-Z_][a-zA-Z0-9_]*)+$""".toRegex()

        // Ссылаемся только на переводы
        if (!text.matches(regex)) {
            return PsiReference.EMPTY_ARRAY
        }

        return arrayOf(KeyPsiReference(element, text))
    }
}
