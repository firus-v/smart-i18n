package com.github.firusv.smarti18n

import com.github.firusv.smarti18n.model.KeyPath
import com.github.firusv.smarti18n.model.TranslationData
import com.github.firusv.smarti18n.model.bus.BusListener


/**
 * Eventbus, который необходим для распределения изменений между участвующими компонентами.
 * Для компонентов, связанных с пользовательским интерфейсом, {@link FilteredDataBus}
 * имеет встроенное решение для применения всех соответствующих фильтров.
 * @author firus-v
 */
open class DataBus {

    private val listeners: MutableSet<BusListener> = HashSet()

    /**
     * Добавляет участника в шину событий. Каждый участник должен быть добавлен вручную.
     * @param listener Слушатель шины
     */
    fun addListener(listener: BusListener) {
        this.listeners.add(listener)
    }

    /**
     * Вызывает соответствующие события на возвращаемом прототипе.
     * Событие будет распределено среди всех участников, зарегистрированных на момент выполнения.
     * @return Прототип слушателя
     */
    fun propagate(): BusListener {
        return object : BusListener {
            override fun onFilterDuplicate(filter: Boolean) {
                listeners.forEach { it.onFilterDuplicate(filter) }
            }

            override fun onFilterIncomplete(filter: Boolean) {
                listeners.forEach { it.onFilterIncomplete(filter) }
            }

            override fun onFocusKey(key: KeyPath) {
                listeners.forEach { it.onFocusKey(key) }
            }

            override fun onSearchQuery(query: String?) {
                listeners.forEach { it.onSearchQuery(query) }
            }

            override fun onUpdateData(data: TranslationData) {
                listeners.forEach { it.onUpdateData(data) }
            }
        }
    }
}