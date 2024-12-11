package com.github.firusv.smarti18n.tabs.renderer

import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.NlsSafe
import javax.swing.JTree


/**
 * Аналогично [NodeRenderer], но переопределит [.getPresentation], чтобы
 * сделать [ItemPresentation] видимым.
 * @author firus-v
 */
class TreeRenderer : NodeRenderer() {
    override fun customizeCellRenderer(
        tree: JTree,
        value: @NlsSafe Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ) {
        super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus)
    }

    override fun getPresentation(node: Any): ItemPresentation? {
        return if (node is ItemPresentation) {
            node
        } else {
            super.getPresentation(node)
        }
    }
}