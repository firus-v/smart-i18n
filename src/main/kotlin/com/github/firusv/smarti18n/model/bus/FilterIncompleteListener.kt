package com.github.firusv.smarti18n.model.bus

/**
 * Слушатель событий
 * @author firus-v
 */
interface FilterIncompleteListener {
    /**
     * Включает фильтр отсутствующих переводов
     * @param filter True, если должны отображаться только переводы с отсутствующими значениями
     */
    fun onFilterIncomplete(filter: Boolean)
}