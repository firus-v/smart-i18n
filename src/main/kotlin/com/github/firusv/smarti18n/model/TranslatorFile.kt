package com.github.firusv.smarti18n.model

import com.intellij.openapi.vfs.VirtualFile


/**
 * Представляет существующий файл перевода в виде строки
 * @author firus-v
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