package com.github.firusv.smarti18n.settings

import com.github.firusv.smarti18n.InstanceManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

/**
 * Конфигуратор панели настроек плагина для IDE
 * @author firus-v
 */
class ProjectSettingsConfigurable(private val project: Project) : Configurable {
    private lateinit var component: ProjectSettingsComponent

    override fun getDisplayName(): String = "Smart I18N"

    override fun createComponent(): JComponent {
        // Поулчаем компоненту настроек
        component = ProjectSettingsComponent(project)
        // Загружаем настройки
        val service = ProjectSettingsService.get(project)
        // Применяем настройки к полям
        component.setState(service.state)

        return component.getMainPanel()
    }

    override fun isModified(): Boolean = true

    override fun apply() {
        val service = ProjectSettingsService.get(project)
        service.state = component.getState()
        InstanceManager.get(project).reload()
    }

    override fun reset() {
        val service = ProjectSettingsService.get(project)
        component.setState(service.state)
    }
}