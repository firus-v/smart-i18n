package com.github.firusv.smarti18n.action

import com.github.firusv.smarti18n.MessagesBundle
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import org.jdesktop.swingx.prompt.PromptSupport
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JPanel
import javax.swing.JComponent
import java.util.function.Consumer

/**
 * Действие для поиска переводов по ключу.
 * @author firus-v
 */
class SearchAction(private val searchCallback: Consumer<String>) : AnAction(MessagesBundle.message("action.search")),
    CustomComponentAction {

    private var textField: JBTextField? = null

    override fun actionPerformed(e: AnActionEvent) {
        // Этот метод не должен быть вызван
    }

    fun actionPerformed() {
        searchCallback.accept(textField?.text ?: "")
    }

    override fun createCustomComponent(presentation: Presentation, place: String): JComponent {
        textField = JBTextField().apply {
            preferredSize = Dimension(160, 25)
            PromptSupport.setPrompt(MessagesBundle.message("action.search"), this)
            border = JBUI.Borders.empty()
            addKeyListener(handleKeyListener())
        }

        val panel = JPanel(BorderLayout()).apply {
            textField?.let { add(it, BorderLayout.CENTER) }
        }

        return panel
    }

    private fun handleKeyListener(): KeyAdapter {
        return object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    e.consume()
                    actionPerformed()
                }
            }
        }
    }
}
