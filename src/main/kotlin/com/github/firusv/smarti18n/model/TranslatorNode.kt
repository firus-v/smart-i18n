package com.github.firusv.smarti18n.model

import java.util.LinkedHashMap
import java.util.TreeMap

/**
 * Узел дерева переводов. Управляет дочерними узлами, которые могут быть переводами
 * или также узлами, ведущими к другому переводу или узлу.
 * Навигация внутри узла может осуществляться как вверх, так и вниз.
 * Для построения полного ключа перевода (full-key) необходимо рекурсивно определить всех родителей.
 *
 * То, должны ли дочерние узлы быть отсортированы, определяется родительским узлом.
 * Для корневых узлов (с пустым родителем) тип [java.util.Map] должен быть указан,
 * чтобы определить, какая сортировка должна применяться.
 * @author @firus-v
 */
class TranslationNode {

    var parent: TranslationNode? = null
        private set

    val children: MutableMap<String, TranslationNode>
    var value: TranslationValue = TranslationValue()
        private set

    constructor(sort: Boolean) : this(if (sort) TreeMap() else LinkedHashMap())

    /**
     * Инициализация корневого узла. Например, используйте [java.util.TreeMap] или [java.util.HashMap].
     * @param children Определяет, какую реализацию использовать (сортированную или несортированную).
     */
    constructor(children: MutableMap<String, TranslationNode>) {
        this.children = children
    }

    /**
     * @return true, если данный узел считается корневым узлом.
     */
    val isRoot: Boolean
        get() = parent == null

    /**
     * @return true, если данный узел не ведет к другим дочерним узлам (содержит только [Translation]).
     * Корневой узел никогда не считается листовым узлом.
     */
    val isLeaf: Boolean
        get() = children.isEmpty() && !isRoot

    fun setParent(parent: TranslationNode?) {
        this.parent = parent
    }

    fun setValue(value: TranslationValue) {
        children.clear()
        this.value = value
    }

    fun setChildren(key: String, node: TranslationNode) {
        node.setParent(this) // Отслеживаем родителя при добавлении дочернего узла
        value.clear()
        children[key] = node
    }

    fun setChildren(key: String): TranslationNode {
        return try {
            val node = TranslationNode(children::class.java.getDeclaredConstructor().newInstance())
            setChildren(key, node)
            node
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Cannot create children of map type ${children::class.java.simpleName}")
        }
    }

    fun setChildren(key: String, translation: TranslationValue) {
        setChildren(key).setValue(translation)
    }

    fun getOrCreateChildren(key: String): TranslationNode {
        return children[key] ?: setChildren(key)
    }

    fun removeChildren(key: String) {
        children.remove(key)
    }

    override fun toString(): String {
        return "TranslationNode{parent=$parent, children=${children.keys}, value=$value}"
    }
}