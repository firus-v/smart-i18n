package com.github.firusv.smarti18n.model.bus


/**
 * Single event listener.
 * @author marhali
 */
interface SearchQueryListener {
    /**
     * Filter the displayed data according to the search query. Supply 'null' to return to the normal state.
     * The keys and the content itself should be considered (full-text-search).
     * @param query Filter key or content
     */
    fun onSearchQuery(query: String?)
}