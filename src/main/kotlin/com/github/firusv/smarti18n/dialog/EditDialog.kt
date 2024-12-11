package com.github.firusv.smarti18n.dialog

import com.github.firusv.smarti18n.MessagesBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.github.firusv.smarti18n.dialog.descriptor.DeleteActionDescriptor
import com.github.firusv.smarti18n.model.action.TranslationDelete
import com.github.firusv.smarti18n.model.action.TranslationUpdate
import com.github.firusv.smarti18n.model.Translation
import javax.swing.*

/**
 * Диалог для редактирования или удаления существующего перевода.
 * @author firus-v
 */
class EditDialog(project: Project, origin: Translation) : TranslationDialog(project, origin) {

    init {
        title = MessagesBundle.message("action.edit")
    }

    override fun createLeftSideActions(): Array<out Action> {
        return arrayOf(DeleteActionDescriptor(this))
    }

    override fun handleExit(exitCode: Int): TranslationUpdate? {
        return when (exitCode) {
            DialogWrapper.OK_EXIT_CODE -> TranslationUpdate(origin, getState())
            DeleteActionDescriptor.EXIT_CODE -> TranslationDelete(origin)
            else -> null
        }
    }
}
