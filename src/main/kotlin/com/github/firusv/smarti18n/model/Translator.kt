package com.github.firusv.smarti18n.model


/**
 * Представляет собой перевод с определенными значениями ключа и локализации
 *
 * @author marhali
 */
class Translation(
    val key: KeyPath, value: TranslationValue?
) {
    private var value: TranslationValue?

    /**
     * Constructs a new translation instance.
     * @param key Absolute key path
     * @param value Values to set - nullable to indicate removal
     */
    init {
        this.value = value
    }

    /**
     * @return values - nullable to indicate removal
     */
    fun getValue(): TranslationValue? {
        return value
    }

    /**
     * @param value Values to set - nullable to indicate removal
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