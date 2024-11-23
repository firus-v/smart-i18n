package com.github.firusv.smarti18n.settings

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.IconUtil
import com.intellij.util.ui.JBUI
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer

class FileListCellRenderer : JLabel(), ListCellRenderer<VirtualFile> {

    init {
        border = JBUI.Borders.empty(5, 10)
    }

    override fun getListCellRendererComponent(
        list: JList<out VirtualFile>,
        value: VirtualFile,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        text = value.path
        icon = IconUtil.getIcon(value, 0, null)

        background = if (isSelected) list.selectionBackground else list.background
        foreground = if (isSelected) list.selectionForeground else list.foreground
        isOpaque = true

        return this
    }
}