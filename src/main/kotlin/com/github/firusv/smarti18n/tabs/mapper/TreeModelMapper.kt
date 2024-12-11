package com.github.firusv.smarti18n.tabs.mapper

import com.github.firusv.smarti18n.model.KeyPath
import com.github.firusv.smarti18n.model.Translation
import com.github.firusv.smarti18n.model.TranslationData
import com.github.firusv.smarti18n.model.TranslationNode
import com.github.firusv.smarti18n.settings.ProjectSettingsState
import com.github.firusv.smarti18n.util.TranslationUtil
import com.github.firusv.smarti18n.util.UiUtil
import com.intellij.ide.projectView.PresentationData
import com.intellij.ui.JBColor
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath


/**
 * Сопоставление [TranslationData] с [TreeModel].
 * @author firus-v
 */
class TreeModelMapper(
    private val data: TranslationData,
    private val state: ProjectSettingsState
) : DefaultTreeModel(null) {

    init {
        val rootNode = DefaultMutableTreeNode("userObject")
        generateNodes(rootNode, KeyPath(), data.rootNode)
        setRoot(rootNode)
    }

    /**
     * @param parent Родительский узел дерева
     * @param translationNode Узел перевода для записи в дерево
     * @return Цвет, который применяется к родительскому узлу
     */
    private fun generateNodes(
        parent: DefaultMutableTreeNode,
        parentPath: KeyPath,
        translationNode: TranslationNode
    ): JBColor? {
        var color: JBColor? = null

        for ((key, childTranslationNode) in translationNode.children) {
            val keyPath = KeyPath(parentPath, key)

            if (!childTranslationNode.isLeaf) { // Вложенный узел — рекурсивный вызов
                val childNode = DefaultMutableTreeNode(key)
                val childColor = generateNodes(childNode, keyPath, childTranslationNode)

                if (childColor != null) {
                    val data = PresentationData(key, null, null, null)
                    data.forcedTextForeground = childColor
                    childNode.userObject = data
                    color = childColor
                }

                parent.add(childNode)
            } else {
                val previewLocale = state.getDefaultLang()
                val sub = "(${previewLocale.selectedItem}: ${childTranslationNode.value.get(previewLocale.selectedItem.toString())})"
                val tooltip = UiUtil.generateHtmlTooltip(childTranslationNode.value.entries)

                val data = PresentationData(key, sub, null, null);
                data.tooltip = tooltip

                if (childTranslationNode.value.size() !== this.data.getLocales().size) {
                    data.forcedTextForeground = JBColor.RED
                    color = JBColor.RED
                }


                parent.add(DefaultMutableTreeNode(data))
            }
        }

        return color
    }

    /**
     * Конвертирует [KeyPath] в [TreePath].
     * @param fullPath Абсолютный путь перевода
     * @return Преобразованный [TreePath]
     */
    fun findTreePath(fullPath: KeyPath): TreePath {
        val nodes = mutableListOf<Any>()
        var currentNode = root as TreeNode
        nodes.add(currentNode)

        for (section in fullPath) {
            currentNode = findNode(currentNode, section!!) ?: break
            nodes.add(currentNode)
        }

        return TreePath(nodes.toTypedArray())
    }

    private fun findNode(parent: TreeNode, key: String): DefaultMutableTreeNode? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)

            if (child is DefaultMutableTreeNode) {
                val childKey = when (val userObject = child.userObject) {
                    is PresentationData -> userObject.presentableText
                    else -> userObject.toString()
                }

                if (childKey == key) {
                    return child
                }
            }
        }

        return null
    }
}