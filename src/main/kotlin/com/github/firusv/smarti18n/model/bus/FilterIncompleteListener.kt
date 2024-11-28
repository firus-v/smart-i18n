package com.github.firusv.smarti18n.model.bus

/**
 * Single event listener.
 * @author marhali
 */
interface FilterIncompleteListener {
    /**
     * Toggles filter of missing translations
     * @param filter True if only translations with missing values should be shown
     */
    fun onFilterIncomplete(filter: Boolean)
}