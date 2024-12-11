package com.github.firusv.smarti18n.model.bus

import com.github.firusv.smarti18n.model.TranslationData


/**
 * Слушатель событий
 * @author firus-v
 */
interface UpdateDataListener {
    /**
     * Обновляет набор переводов.
     * @param data обновленные переводы
     */
    fun onUpdateData(data: TranslationData)
}