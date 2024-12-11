package com.github.firusv.smarti18n.model.bus


/**
 * Просулушиватьель событий
 * @see .onFilterDuplicate
 * @author firus-v
 */
interface FilterDuplicateListener {
    /**
     * Включает фильтр дублированных значений перевода
     * @param filter True, если должны отображаться только переводы с дублирующимися значениями
     */
    fun onFilterDuplicate(filter: Boolean)
}