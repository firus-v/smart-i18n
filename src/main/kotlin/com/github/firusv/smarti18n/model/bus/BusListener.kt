package com.github.firusv.smarti18n.model.bus

/**
 * Интерфейс для передачи изменений для шины данных.
 * Каждый слушатель должен быть зарегистрирован через [com.github.firusv.smarti18n.DataBus].
 *
 * @author firus-v
 */
interface BusListener : UpdateDataListener, FilterIncompleteListener, FilterDuplicateListener, SearchQueryListener,
    FocusKeyListener