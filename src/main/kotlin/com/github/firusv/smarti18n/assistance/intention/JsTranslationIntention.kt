package com.github.firusv.smarti18n.assistance.intention

import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * Специфичная для JavaScript интенция для ключа перевода.
 * @author firus-v
 */
class JsTranslationIntention : AbstractTranslationIntention() {

    override fun extractText(@NotNull element: PsiElement): @Nullable String? {
        if (element.parent !is JSLiteralExpression) {
            return null
        }

        return (element.parent as JSLiteralExpression).stringValue
    }

     override fun convertRange(@NotNull input: TextRange): @NotNull TextRange {
        return TextRange(input.startOffset + 1, input.endOffset - 1)
    }
}