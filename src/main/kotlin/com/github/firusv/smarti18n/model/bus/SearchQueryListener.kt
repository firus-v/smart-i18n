package com.github.firusv.smarti18n.model.bus


/**
 * Слушатель событий
 * @author firus-v
 */
interface SearchQueryListener {
    /**
     * Фильтрация отображаемых данных в соответствии с поисковым запросом. Передать 'null' для возврата в нормальное состояние.
     * полный текстовый поиск.
     * @param query Фильтрация ключа или содержимого
     */
    fun onSearchQuery(query: String?)
}