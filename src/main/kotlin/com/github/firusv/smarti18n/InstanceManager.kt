package com.github.firusv.smarti18n

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import java.util.*

/**
 * Central singleton component for managing an easy-i18n instance for a specific project.
 * @author marhali
 */
class InstanceManager private constructor(project: Project) {
    private val store: DataStore
    private val bus: DataBus
    private val uiBus: FilteredDataBus

    init {
        this.store = DataStore(project)
        this.bus = DataBus()
        this.uiBus = FilteredDataBus(project)

        // Register ui eventbus on top of the normal eventbus
        bus.addListener(this.uiBus)

        // Load data after first initialization
        ApplicationManager.getApplication().invokeLater {
            store.loadFromPersistenceLayer { success ->
                bus.propagate()
                    .onUpdateData(store.getData())
            }
        }
    }

    fun store(): DataStore {
        return this.store
    }

    /**
     * Primary eventbus.
     */
    fun bus(): DataBus {
        return this.bus
    }

    /**
     * UI optimized eventbus with builtin filter logic.
     */
    fun uiBus(): FilteredDataBus {
        return this.uiBus
    }

    /**
     * Reloads the plugin instance. Unsaved cached data will be deleted.
     * Fetches data from persistence layer and notifies all endpoints via [DataBus].
     */
    fun reload() {
        store.loadFromPersistenceLayer { success -> bus.propagate().onUpdateData(store.getData()) }
    }

    fun processUpdate(update: TranslationUpdate) {
        if (update.isDeletion() || update.isKeyChange()) { // Remove origin translation
            store.getData().setTranslation(update.getOrigin().getKey(), null)
        }

        if (!update.isDeletion()) { // Create or re-create translation with changed data
            store.getData().setTranslation(update.getChange().getKey(), update.getChange().getValue())
        }

        store.saveToPersistenceLayer { success ->
            if (success) {
                bus.propagate().onUpdateData(store.getData())

                if (!update.isDeletion()) {
                    bus.propagate().onFocusKey(update.getChange().getKey())
                } else {
                    bus.propagate().onFocusKey(update.getOrigin().getKey())
                }
            }
        }
    }

    companion object {
        private val INSTANCES: MutableMap<Project, InstanceManager> = WeakHashMap()

        fun get(project: Project): InstanceManager {
            var instance = INSTANCES[project]

            if (instance == null) {
                instance = InstanceManager(project)
                INSTANCES[project] = instance
            }

            return instance
        }
    }
}