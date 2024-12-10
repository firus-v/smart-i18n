package com.github.firusv.smarti18n.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(name = "ProjectSettingsService", storages = [Storage("smart-i18n.xml")])
class ProjectSettingsService : PersistentStateComponent<ProjectSettingsState> {
    private var state: ProjectSettingsState

    init {
        this.state = ProjectSettingsState()
    }

    // Сохраняет настройки (apply)
    fun setState(state: ProjectSettingsState) {
        this.state = state
    }

    // Получает стейт из сохранений (для reset)
    override fun getState(): ProjectSettingsState {
        return state
    }

    override fun loadState(state: ProjectSettingsState) {
        this.state = state
    }

    companion object {
        fun get(project: Project): ProjectSettingsService {
            return project.getService(ProjectSettingsService::class.java)
        }
    }
}