package com.github.firusv.smarti18n.model.bus

/**
 * Прослушиватель отдельных событий.
 * @see .onExpandAll
 * @author firusv
 */
interface ExpandAllListener {
    /**
     * Действие раскрытия всех узлов
     */
    fun onExpandAll()
}