package com.github.firusv.smarti18n.model


/**
 * Представляет собой перевод с определенными значениями ключа и локализации
 *
 * @author firus-v
 */
class Translation(
    val key: KeyPath, value: TranslationValue?
) {
    private var value: TranslationValue?

    /**
     * Собирает новый экземпляр перевода
     * @param key Абсолютный путь
     * @param value значение, null - указывает, что требуется удаление
     */
    init {
        this.value = value
    }

    /**
     * @return значение, null - указывает, что требуется удаление
     */
    fun getValue(): TranslationValue? {
        return value
    }

    /**
     * @param value значение для установки перевода, null - указывает, что требуется удаление
     */
    fun setValue(value: TranslationValue?) {
        this.value = value
    }

    override fun toString(): String {
        return "Translation{" +
                "key=" + key +
                ", value=" + value +
                '}'
    }
}