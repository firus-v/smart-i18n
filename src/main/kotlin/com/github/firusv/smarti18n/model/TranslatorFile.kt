package com.github.firusv.smarti18n.model

import com.intellij.openapi.vfs.VirtualFile


/**
 * Represents an existing translation file in a context a specific folder strategy.
 * @author marhali
 */
class TranslationFile(val virtualFile: VirtualFile, val locale: String, val namespace: KeyPath?) {
    override fun toString(): String {
        return "TranslationFile{" +
                "virtualFile=" + virtualFile +
                ", locale='" + locale + '\'' +
                ", namespace='" + namespace + '\'' +
                '}'
    }
}