package com.github.firusv.smarti18n.listeners

import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.util.function.Consumer


/**
 * Слушатель щелчка всплывающего окна для awt [MouseListener].
 * Эммитит событие при открытии всплывающего окна.
 * @author firus-v
 */
class PopupClickListener(private val callback: Consumer<MouseEvent>) : MouseListener {
    override fun mouseClicked(e: MouseEvent) {}

    override fun mousePressed(e: MouseEvent) {
        if (e.isPopupTrigger) {
            callback.accept(e)
        }
    }

    override fun mouseReleased(e: MouseEvent) {
        if (e.isPopupTrigger) {
            callback.accept(e)
        }
    }

    override fun mouseEntered(e: MouseEvent) {}

    override fun mouseExited(e: MouseEvent) {}
}