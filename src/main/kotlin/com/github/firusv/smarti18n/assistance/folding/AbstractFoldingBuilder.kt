package com.github.firusv.smarti18n.assistance.folding
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.github.firusv.smarti18n.DataStore
import com.github.firusv.smarti18n.InstanceManager
import com.github.firusv.smarti18n.assistance.completion.OptionalAssistance
import com.github.firusv.smarti18n.model.TranslationData
import com.github.firusv.smarti18n.model.TranslationValue
import com.github.firusv.smarti18n.settings.ProjectSettingsState
import com.github.firusv.smarti18n.settings.ProjectSettingsService
import com.github.firusv.smarti18n.util.KeyPathConverter

/**
 * Свертка ключей перевода для конкретного языка с представительным значением локали.
 * @author firus-v
 */
abstract class AbstractFoldingBuilder : FoldingBuilderEx(), OptionalAssistance {

    /**
     * Извлечение всех релевантных областей для свертки из корневого элемента.
     * Реализация не обязана проверять, является ли текстовый литерал допустимым переводом.
     * @param root Корневой элемент
     * @return найденные области
     */
    abstract fun extractRegions(root: PsiElement): List<Pair<String, PsiElement>>

    /**
     * Извлечение текста из указанного узла.
     * @param node Узел
     * @return извлеченный текст или null, если не применимо
     */
    abstract fun extractText(node: ASTNode): String?

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        if (quick || !isAssistance(root.project)) {
            return FoldingDescriptor.EMPTY_ARRAY
        }

        val descriptors = mutableListOf<FoldingDescriptor>()

        val settings: ProjectSettingsState = ProjectSettingsService.get(root.project).state
        val data: TranslationData = InstanceManager.get(root.project).store().getData()
        val converter = KeyPathConverter(settings)

        for (region in extractRegions(root)) {
            val key = region.first
            if(key === null){
                continue
            }
            val psiElement = region.second
            val translation = converter.fromString(key)

            if (data.getTranslation(translation) == null) {
                continue
            }

            val range = TextRange(
                psiElement.textRange.startOffset + 1,
                psiElement.textRange.endOffset - 1
            )

            val isAlwaysFold = settings.getAlwaysFoldingTranslate()

            // Некоторые реализации языков, такие как [Vue Template], не поддерживают FoldingGroup
            val descriptor = FoldingDescriptor(
                psiElement.node,
                range,
                null,
                emptySet(),
                isAlwaysFold
            )

            descriptors.add(descriptor)
        }

        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? {
        val text = extractText(node) ?: return null

        val project: Project = node.psi.project
        val store: DataStore = InstanceManager.get(project).store()
        val converter = KeyPathConverter(project)
        val keyText = converter.fromString(text);
        val localeValues: TranslationValue? = store.getData().getTranslation(keyText)

        if (localeValues == null) {
            return null
        }

        val previewLocale = ProjectSettingsService.get(project).state.getDefaultLang().selectedItem.toString()
        return localeValues.get(previewLocale)
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return true
    }
}