package com.github.firusv.smarti18n

import com.github.firusv.smarti18n.model.action.TranslationUpdate
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull
import java.util.*

/**
 * Центральный синглтон для управления экземпляром smart-i18n для конкретного проекта.
 * @author firus-v
 */
class InstanceManager private constructor(@NotNull val  project: Project) {

    private val store: DataStore = DataStore(project)
    private val bus: DataBus = DataBus()
    private val uiBus: FilteredDataBus = FilteredDataBus(project)

    init {
        // Регистрация UI eventbus поверх основного eventbus
        bus.addListener(uiBus)

        // Загрузка данных после первой инициализации
        ApplicationManager.getApplication().invokeLater {
            store.loadFromPersistenceLayer { success ->
                bus.propagate().onUpdateData(store.getData())
            }
        }
    }

    fun store(): DataStore = store

    /**
     * Основной eventbus.
     */
    fun bus(): DataBus = bus

    /**
     * Оптимизированный UI eventbus с встроенной логикой фильтрации.
     */
    fun uiBus(): FilteredDataBus = uiBus

    /**
     * Перезагружает экземпляр плагина. Несохранённые кэшированные данные будут удалены.
     * Загружает данные из слоя хранения и уведомляет все конечные точки через {@link DataBus}.
     */
    fun reload() {
        store.loadFromPersistenceLayer { success ->
            bus.propagate().onUpdateData(store.getData())
        }
    }

    fun processUpdate(update: TranslationUpdate) {
        if (update.isDeletion || update.isKeyChange) { // Удаление оригинального перевода
            update.origin?.key?.let { store.getData().setTranslation(it, null) }
        }

        if (!update.isDeletion) { // Создание или обновление перевода с изменёнными данными
            update.change?.let {
                store.getData().setTranslation(it.key, it.getValue())
            }
        }

        store.saveToPersistenceLayer { success ->
            if (success) {
                bus.propagate().onUpdateData(store.getData())

                val keyToFocus = if (!update.isDeletion) {
                    update.change?.key
                } else {
                    update.origin?.key
                }

                keyToFocus?.let { bus.propagate().onFocusKey(it) }
            }
        }
    }

    companion object {
        private val INSTANCES = WeakHashMap<Project, InstanceManager>()

        fun get(@NotNull project: Project): InstanceManager {
            return INSTANCES.getOrPut(project) { InstanceManager(project) }
        }
    }
}