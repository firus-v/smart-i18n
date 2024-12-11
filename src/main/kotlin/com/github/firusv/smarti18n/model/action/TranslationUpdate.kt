package com.github.firusv.smarti18n.model.action

import com.github.firusv.smarti18n.model.Translation


/**
 * Представляет обновление для переведенного i18n ключа.
 * Поддерживает создание, изменение и удаление переводов.
 *
 * @author firus-v
 */
open class TranslationUpdate(val origin: Translation?, val change: Translation?) {
    val isCreation: Boolean
        get() = this.origin == null

    val isDeletion: Boolean
        get() = this.change == null

    val isKeyChange: Boolean
        get() = this.origin != null && this.change != null && origin.key != change.key

    override fun toString(): String {
        return "TranslationUpdate{" +
                "origin=" + origin +
                ", change=" + change +
                '}'
    }
}