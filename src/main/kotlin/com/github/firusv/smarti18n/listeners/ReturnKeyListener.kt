package com.github.firusv.smarti18n.listeners

import java.awt.event.KeyEvent
import java.awt.event.KeyListener


/**
 * Return (\n) keystroke listener.
 * @author firusv
 */
class ReturnKeyListener(private val onActivate: Runnable) : KeyListener {
    override fun keyTyped(e: KeyEvent) {
        if (e.keyChar.code == KeyEvent.VK_ENTER) {
            onActivate.run()
        }
    }

    override fun keyPressed(e: KeyEvent) {}

    override fun keyReleased(e: KeyEvent) {}
}