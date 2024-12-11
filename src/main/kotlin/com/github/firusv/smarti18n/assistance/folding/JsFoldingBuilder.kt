package com.github.firusv.smarti18n.assistance.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.openapi.util.Pair
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * Свертка ключей перевода для JavaScript.
 * @author firus-v
 */
class JsFoldingBuilder : AbstractFoldingBuilder() {

    override fun extractRegions(root: PsiElement): List<Pair<String, PsiElement>> {
        return PsiTreeUtil.findChildrenOfType(root, JSLiteralExpression::class.java)
            .map { literalExpression ->
                Pair.create(literalExpression.stringValue, literalExpression as PsiElement)
            }
    }

    override fun extractText(node: ASTNode): String? {
        val literalExpression = node.psi as? JSLiteralExpression
        return literalExpression?.stringValue
    }
}