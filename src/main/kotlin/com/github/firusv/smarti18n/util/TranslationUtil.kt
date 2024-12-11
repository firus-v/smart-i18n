package com.github.firusv.smarti18n.util

import com.github.firusv.smarti18n.model.Translation
import com.github.firusv.smarti18n.model.TranslationData
import com.github.firusv.smarti18n.model.TranslationValue
import com.github.firusv.smarti18n.settings.ProjectSettingsState

/**
 * Утилиты для работы с переводами.
 * @author firus-v
 */
object TranslationUtil {

    /**
     * Проверяет, содержит ли данный перевод дублирующиеся значения.
     * @param translation Перевод для проверки
     * @param data Кэш данных переводов
     * @return true, если найдены дубликаты, иначе false
     */
    fun hasDuplicates(translation: Translation, data: TranslationData): Boolean {
        val contents = translation.getValue()?.localeContents ?: return false

        for (key in data.fullKeys) {
            if (translation.key == key) { // Учитываем только другие переводы
                continue
            }

            val otherContents = data.getTranslation(key)?.localeContents ?: continue
            if (contents.any { it in otherContents }) {
                return true
            }
        }

        return false
    }

    /**
     * Проверяет, содержит ли перевод отсутствующие значения локалей.
     * @param value Перевод для проверки
     * @param data Кэш данных переводов
     * @return true, если найдены отсутствующие значения, иначе false
     */
    fun isIncomplete(value: TranslationValue, data: TranslationData): Boolean {
        return value.localeContents.size != data.getLocales().size ||
                value.localeContents.any { it.isEmpty() }
    }

    /**
     * Проверяет, относится ли данный перевод к указанному поисковому запросу.
     * @param settings Настройки проекта
     * @param translation Перевод для проверки
     * @param searchQuery Текст поискового запроса
     * @return true, если перевод соответствует запросу, иначе false
     */
    fun isSearched(settings: ProjectSettingsState, translation: Translation, searchQuery: String): Boolean {
        val concatKey = KeyPathConverter(settings).toString(translation.key).lowercase()

        if (searchQuery.contains(concatKey) || concatKey.contains(searchQuery)) {
            return true
        }

        val contents = translation.getValue()?.localeContents ?: return false
        return contents.any { it.lowercase().contains(searchQuery) }
    }
}