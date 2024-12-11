package com.github.firusv.smarti18n

import com.github.firusv.smarti18n.model.KeyPath
import com.github.firusv.smarti18n.model.Translation
import com.github.firusv.smarti18n.model.TranslationData
import com.github.firusv.smarti18n.model.TranslationNode
import com.github.firusv.smarti18n.model.bus.BusListener
import com.github.firusv.smarti18n.model.bus.FilteredBusListener
import com.github.firusv.smarti18n.settings.ProjectSettingsService
import com.github.firusv.smarti18n.settings.ProjectSettingsState
import com.github.firusv.smarti18n.util.TranslationUtil
import com.intellij.openapi.project.Project

/**
 * Шина событий, связанная с пользовательским интерфейсом. Использует {@link BusListener}
 * из {@link DataBus} под капотом.
 * Компоненты пользовательского интерфейса (например, вкладки) используют этот компонент,
 * реализуя {@link FilteredBusListener}.
 * @author firus-v
 */
class FilteredDataBus(private val project: Project) : BusListener {

    private val listeners: MutableSet<FilteredBusListener> = HashSet()

    private var data: TranslationData? = null
    private var settings: ProjectSettingsState? = null
    private var filterDuplicate: Boolean = false
    private var filterIncomplete: Boolean = false
    private var searchQuery: String? = null
    private var focusKey: KeyPath? = null

    /**
     * Конструктор новой шины событий, специфичной для проекта.
     * @param project Связанный проект
     */
    fun addListener(listener: FilteredBusListener) {
        this.listeners.add(listener)
    }

    override fun onFilterDuplicate(filter: Boolean) {
        this.filterDuplicate = filter
        this.processAndPropagate()
        fire { it.onExpandAll() }
    }

    override fun onFilterIncomplete(filter: Boolean) {
        this.filterIncomplete = filter
        this.processAndPropagate()
        fire { it.onExpandAll() }
    }

    override fun onFocusKey(key: KeyPath) {
        this.focusKey = key
        fire { it.onFocusKey(key) }
    }

    override fun onSearchQuery(query: String?) {
        this.searchQuery = query?.lowercase()
        this.processAndPropagate()
        fire { it.onExpandAll() }
    }

    override fun onUpdateData(data: TranslationData) {
        this.data = data
        this.settings = ProjectSettingsService.get(this.project).state
        processAndPropagate()
    }

    /**
     * Фильтрует переводы на основе заданных фильтров и распространяет изменения
     * на всех зарегистрированных участников.
     * Внутренне создает неглубокую копию кешированных переводов и
     * удаляет все, что не соответствует настроенным фильтрам.
     */
    private fun processAndPropagate() {
        val locales: MutableSet<String> = this.data!!.getLocales().toMutableSet()
        val shadow = TranslationData(locales, TranslationNode(this.data!!.isSorting)
        )

        for (key in this.data!!.fullKeys) {
            val value = this.data!!.getTranslation(key) ?: continue

            // Создаем неглубокую копию текущего перевода
            // и удаляем все переводы, которые не соответствуют фильтрам
            shadow.setTranslation(key, value)

            // Фильтр неполных переводов
            if (filterIncomplete && !TranslationUtil.isIncomplete(value, this.data!!)) {
                shadow.setTranslation(key, null)
                continue
            }

            // Фильтр дублирующихся значений
            if (filterDuplicate && !TranslationUtil.hasDuplicates(Translation(key, value), this.data!!)) {
                shadow.setTranslation(key, null)
                continue
            }

            // Полнотекстовый поиск
            if (searchQuery != null && !TranslationUtil.isSearched(settings!!, Translation(key, value), searchQuery!!)) {
                shadow.setTranslation(key, null)
            }
        }

        fire { listener ->
            listener.onUpdateData(shadow)
            focusKey?.let { listener.onFocusKey(it) }
        }
    }

    /**
     * Уведомляет всех зарегистрированных участников о произошедшем событии.
     * @param action Действие для каждого участника
     */
    private fun fire(action: (FilteredBusListener) -> Unit) {
        listeners.forEach(action)
    }
}