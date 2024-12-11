package com.github.firusv.smarti18n.util

import com.github.firusv.smarti18n.model.KeyPath
import com.intellij.ide.projectView.PresentationData
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath


/**
 * Преобразователь для деверва
 * @author firus-v
 */
object TreeUtil {
    /**
     * Конвертирует путь дерева и возвращает [TreePath]
     * @param treePath TreePath
     * @return Конвертированный keyPath
     */
    fun getFullPath(treePath: TreePath): KeyPath {
        val keyPath = KeyPath()

        for (obj in treePath.getPath()) {
            val node = obj as DefaultMutableTreeNode
            val value = node.userObject
            val section = if (value is PresentationData) value.presentableText else value.toString()

            if (value == null) { // Skip empty sections
                continue
            }

            keyPath.add(section)
        }

        return keyPath
    }
}