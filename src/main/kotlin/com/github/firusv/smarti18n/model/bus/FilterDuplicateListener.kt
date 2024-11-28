package com.github.firusv.smarti18n.model.bus


/**
 * Single event listener
 * @see .onFilterDuplicate
 * @author marhali
 */
interface FilterDuplicateListener {
    /**
     * Toggles filter of duplicated translation values
     * @param filter True if only translations with duplicates values should be shown
     */
    fun onFilterDuplicate(filter: Boolean)
}