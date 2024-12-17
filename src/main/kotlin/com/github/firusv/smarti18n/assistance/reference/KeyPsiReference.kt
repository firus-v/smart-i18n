package com.github.firusv.smarti18n.assistance.reference

import com.github.firusv.smarti18n.settings.ProjectSettingsService
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase

class KeyPsiReference(
    element: PsiElement,
    private val key: String
) : PsiReferenceBase<PsiElement>(element, TextRange(1, element.textLength - 1)) {

    override fun resolve(): PsiElement? {
        val project = element.project
        val jsonFiles = ProjectSettingsService.get(project).state.getFileListModel()

        for (index in 0 until jsonFiles.size) {
            val file = jsonFiles.getElementAt(index);
            val psiFile = PsiManager.getInstance(project).findFile(file) as? JsonFile ?: continue
            val target = findKeyInJson(psiFile, key.split("."))
            if (target != null) return target
        }
        return null
    }

    private fun findKeyInJson(jsonFile: JsonFile, path: List<String>): PsiElement? {
        if (path.isEmpty()) return null

        val rootObject = jsonFile.topLevelValue as? JsonObject ?: return null
        var currentObject: JsonObject = rootObject
        var result: PsiElement? = null

        for (key in path) {
            val property = currentObject.findProperty(key) ?: return null
            val value = property.value

            // Если мы достигли последнего ключа в пути, возвращаем найденное значение
            if (key == path.last()) {
                result = property.nameElement
                break
            }

            // Если значение — объект, продолжаем поиск внутри него
            currentObject = value as? JsonObject ?: return null
        }

        return result
    }

    override fun getVariants(): Array<Any> {
        // Реализуйте автодополнение при необходимости
        return emptyArray()
    }
}