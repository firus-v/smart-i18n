package com.github.firusv.smarti18n.model.bus

import com.github.firusv.smarti18n.model.KeyPath


/**
 * Прослушиватель отдельных событий.
 * @author firus-v
 */
interface FocusKeyListener {
    /**
     * Перемещает указанный ключ перевода (полный ключ) в фокус.
     * * @param key Абсолютный ключ перевода
     */
    fun onFocusKey(key: KeyPath)
}