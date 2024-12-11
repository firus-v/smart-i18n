package com.github.firusv.smarti18n.dialog.descriptor

import com.github.firusv.smarti18n.MessagesBundle
import com.intellij.openapi.ui.DialogWrapper
import java.awt.event.ActionEvent
import javax.swing.AbstractAction

/**
 * Действие для удаления, которое представляет кнопку удаления в диалоге редактирования перевода.
 * Действие можно отслеживать, используя код выхода для открытого диалога. См. EXIT_CODE.
 * @author firus-v
 */
class DeleteActionDescriptor(private val dialog: DialogWrapper) : AbstractAction(
    MessagesBundle.message("action.delete")
) {

    companion object {
        const val EXIT_CODE = 10
    }

    override fun actionPerformed(e: ActionEvent) {
        dialog.close(EXIT_CODE, false)
    }
}
