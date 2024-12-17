package com.github.firusv.smarti18n.assistance.reference

import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.openapi.project.Project
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import org.jetbrains.annotations.NotNull

/**
 * Привязка ссылок на ключи перевода, специфичная для JavaScript.
 * @author firus-v
 */
class JsKeyReferenceContributor : AbstractKeyReferenceContributor() {

    override fun registerReferenceProviders(@NotNull registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(JSLiteralExpression::class.java).withText(StandardPatterns.string().contains(".")),
            provider
        )
    }

    private val provider: PsiReferenceProvider
        get() = object : PsiReferenceProvider() {
            override fun getReferencesByElement(
                @NotNull element: PsiElement,
                @NotNull context: ProcessingContext
            ): Array<PsiReference> {
                val literalExpression = element as JSLiteralExpression
                val value: String = literalExpression.stringValue ?: ""

                return getReferences(element.project, literalExpression, value)
            }
        }
}