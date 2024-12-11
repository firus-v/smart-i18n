package com.github.firusv.smarti18n.assistance.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.patterns.PlatformPatterns


/**
 * Поставщик автодополнения для JavaScript
 * @author firus-v
 */
class JsCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC, PlatformPatterns.psiElement().inside(
                JSLiteralExpression::class.java
            ),
            KeyCompletionProvider()
        )
    }
}