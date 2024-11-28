package com.github.firusv.smarti18n.model.bus

/**
 * Interface for communication of changes for participants of the data bus.
 * Every listener needs to be registered via [de.marhali.easyi18n.DataBus].
 *
 * @author marhali
 */
interface BusListener : UpdateDataListener, FilterIncompleteListener, FilterDuplicateListener, SearchQueryListener,
    FocusKeyListener