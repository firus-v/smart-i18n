package com.github.firusv.smarti18n

import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull
import java.util.*

/**
 * Центральный синглтон для управления экземпляром smart-i18n для конкретного проекта.
 * @author firus-v
 */
class InstanceManager private constructor(@NotNull project: Project) {

    /**
     * Перезагружает экземпляр плагина.
     */
    fun reload() {
    }

    companion object {
        private val INSTANCES = WeakHashMap<Project, InstanceManager>()

        fun get(@NotNull project: Project): InstanceManager {
            return INSTANCES.getOrPut(project) { InstanceManager(project) }
        }
    }
}