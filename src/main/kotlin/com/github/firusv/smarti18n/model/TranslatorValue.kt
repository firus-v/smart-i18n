package com.github.firusv.smarti18n.model


/**
 * Представляет собой набор значений, лежащих в основе конкретного перевода.
 * @author firus-v
 */
class TranslationValue() {
    private var localeValues: MutableMap<String, String>

    init {
        this.localeValues = HashMap()
    }

    constructor(locale: String, content: String) : this() {
        localeValues[locale] = content
    }

    val entries: Set<Map.Entry<String, String>>
        get() = localeValues.entries

    val localeContents: Collection<String>
        get() = localeValues.values

    fun setLocaleValues(localeValues: MutableMap<String, String>) {
        this.localeValues = localeValues
    }

    fun get(locale: String): String? {
        return localeValues[locale]
    }

    fun put(locale: String, content: String) {
        localeValues[locale] = content
    }

    fun remove(locale: String) {
        localeValues.remove(locale)
    }

    fun containsLocale(locale: String): Boolean {
        return localeValues.containsKey(locale)
    }

    fun size(): Int {
        return localeValues.size
    }

    fun clear() {
        localeValues.clear()
    }

    override fun toString(): String {
        return "TranslationValue{" +
                "localeValues=" + localeValues +
                '}'
    }
}