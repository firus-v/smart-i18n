package com.github.firusv.smarti18n.assistance.intention

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import com.github.firusv.smarti18n.InstanceManager
import com.github.firusv.smarti18n.MessagesBundle
import com.github.firusv.smarti18n.assistance.OptionalAssistance
import com.github.firusv.smarti18n.dialog.AddDialog
import com.github.firusv.smarti18n.dialog.EditDialog
import com.github.firusv.smarti18n.model.KeyPath
import com.github.firusv.smarti18n.model.Translation
import com.github.firusv.smarti18n.settings.ProjectSettingsService
import com.github.firusv.smarti18n.util.KeyPathConverter
import java.util.ResourceBundle
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * Интенция для работы с переводами.
 * Может использоваться для извлечения (создания) переводов или редактирования существующих.
 * @author firus-v
 */
abstract class AbstractTranslationIntention : BaseElementAtCaretIntentionAction(), OptionalAssistance {

    private var existingTranslation = false

    override fun getText(): @IntentionName @NotNull String {
        return if (existingTranslation) {
            MessagesBundle.message("action.edit-translate")
        } else {
            MessagesBundle.message("action.add-translate")
        }
    }

    override fun getFamilyName(): @NotNull @IntentionFamilyName String {
        return "SmartI18n"
    }

    override fun startInWriteAction(): Boolean {
        return false
    }

    /**
     * Это единственный метод, который должна реализовать конкретная интенция для языка.
     * Реализация должна проверять тип элемента и извлекать соответствующий ключ или значение.
     * @param element Элемент на позиции курсора
     * @return Извлечённый ключ перевода (не проверяется!) или null, если интенция не применима к этому элементу
     */
    protected abstract fun extractText(@NotNull element: PsiElement): @Nullable String?

    open fun convertRange(@NotNull input: TextRange): TextRange {
        return TextRange(input.startOffset, input.endOffset)
    }

    override fun isAvailable(@NotNull project: Project, editor: Editor, @NotNull element: PsiElement): Boolean {
        if (!isAssistance(project)) {
            return false
        }

        val text = extractText(element)

        if (text != null) {
            val converter = KeyPathConverter(project)
            existingTranslation = InstanceManager.get(project).store().getData()
                .getTranslation(converter.fromString(text)) != null
        }

        return text != null
    }

    override fun invoke(@NotNull project: Project, editor: Editor, @NotNull element: PsiElement) {
        val settings = ProjectSettingsService.get(project).state
        val converter = KeyPathConverter(settings)

        val text = extractText(element)
            ?: throw IncorrectOperationException("Не удается извлечь интенцию перевода на позиции курсора")

        val data = InstanceManager.get(project).store().getData()
        val path = converter.fromString(text)
        val existingTranslation = data.getTranslation(path)

        // Существующий перевод - редактирование
        if (existingTranslation != null) {
            EditDialog(project, Translation(path, existingTranslation)).showAndHandle()
            return
        }

        val delimiter = settings.getDelimiter()

        val regex = """^([a-zA-Z_][a-zA-Z0-9_]*)([${delimiter}][a-zA-Z_][a-zA-Z0-9_]*)+$""".toRegex()


        // Извлечение перевода по ключу
        if (text.matches(regex)) {
            AddDialog(project, path, null).showAndHandle()
            return
        }

        // Извлечение перевода по значению для предварительного локализованного текста
        val dialog = AddDialog(project, KeyPath(), text)

        dialog.registerCallback { translationUpdate ->
            // Заменить текст на позиции курсора на выбранный ключ перевода
            editor.let {
                val doc: Document = it.document
                val caret: Caret = it.caretModel.primaryCaret
                val range: TextRange = convertRange(element.textRange)

                WriteCommandAction.runWriteCommandAction(project) {
                    doc.replaceString(
                        range.startOffset,
                        range.endOffset,
                        converter.toString(translationUpdate.change!!.key)
                    )
                }

                caret.removeSelection()
            }
        }

        dialog.showAndHandle()
    }
}
