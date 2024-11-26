package com.github.firusv.smarti18n.model.bus

import com.github.firusv.smarti18n.model.TranslationData


/**
 * Single event listener.
 * @author marhali
 */
interface UpdateDataListener {
    /**
     * Update the underlying translation data set.
     * @param data Updated translations
     */
    fun onUpdateData(data: TranslationData)
}