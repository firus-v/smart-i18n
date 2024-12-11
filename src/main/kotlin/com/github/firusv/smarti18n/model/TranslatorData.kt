package com.github.firusv.smarti18n.model

import java.util.*

/**
 * Кэшированные данные переводов. Данные хранятся в виде древовидной структуры.
 * Поведение дерева (сортированное или несортированное) можно указать через конструктор.
 * Подробнее см. [TranslationNode]. Пример отображения дерева:
 * <pre>
 * `user:
 * principal: 'Principal'
 * username:
 * title: 'Username'
 * auth:
 * logout: 'Logout'
 * login: 'Login'
` *
</pre> *
 * @author @firus-v
 */
class TranslationData
/**
 * @param locales Языки, которые могут быть использованы для перевода
 * @param rootNode Структура дерева переводов
 */(
    private val locales: MutableSet<String>,
    /**
     * @return Корневой узел, содержащий все переводы
     */
    val rootNode: TranslationNode
) {
    /**
     * Создаёт пустой экземпляр.
     * @param sort Должны ли ключи переводов сортироваться в алфавитном порядке
     */
    constructor(sort: Boolean) : this(
        HashSet<String>(),
        TranslationNode(if (sort) TreeMap<String, TranslationNode>() else LinkedHashMap<String, TranslationNode>())
    )

    /**
     * @return Набор языков, на которые могут быть выполнены переводы
     */
    fun getLocales(): Set<String> {
        return this.locales
    }

    /**
     * @param locale Добавляет указанный язык в список поддерживаемых
     */
    fun addLocale(locale: String) {
        locales.add(locale)
    }

    /**
     * @param fullPath Абсолютный путь к переводу
     * @return Узел перевода, который ведёт к переводам или вложенным дочерним элементам
     */
    fun getNode(fullPath: KeyPath): TranslationNode? {
        var node: TranslationNode? = this.rootNode

        if (fullPath.isEmpty()) { // Возвращает корневой узел, если путь пустой
            return node
        }

        for (section in fullPath) {
            if (node == null) {
                return null
            }
            node = node.children[section]
        }

        return node
    }

    fun getOrCreateNode(fullPath: KeyPath): TranslationNode {
        var node = this.rootNode

        if (fullPath.isEmpty()) { // Возвращает корневой узел, если путь пустой
            return node
        }

        for (section in fullPath) {
            node = node.getOrCreateChildren(section!!)
        }

        return node
    }

    /**
     * @param fullPath Абсолютный путь ключа перевода
     * @return Найденный перевод. Может быть null, если путь пуст или не является листовым элементом
     */
    fun getTranslation(fullPath: KeyPath): TranslationValue? {
        val node = this.getNode(fullPath)

        if (node == null || !node.isLeaf) {
            return null
        }

        return node.value
    }

    /**
     * Создаёт / обновляет или удаляет определённый перевод.
     * Родительский путь перевода будет изменён при необходимости.
     * @param fullPath Абсолютный путь ключа перевода
     * @param translation Перевод для установки. Может быть null для удаления соответствующего узла
     */
    fun setTranslation(fullPath: KeyPath, translation: TranslationValue?) {
        var fullPath = fullPath
        if (fullPath.isEmpty()) { // Пропустить пустые ключи перевода
            return
        }

        fullPath = KeyPath(fullPath)
        val leafKey =
            fullPath.removeAt(fullPath.size - 1) // Извлекает крайний раздел в качестве ключа дочернего элемента родителя
        var node = this.rootNode

        for (section in fullPath) { // Переход к вложенному уровню по @leafKey
            var childNode = node.children[section]

            if (childNode == null) {
                if (translation == null) { // Путь не должен быть пустым при удалении
                    // Раздел уже удалён / не существует
                    return
                }

                childNode = node.setChildren(section!!)
            }

            node = childNode
        }

        if (translation == null) { // Удаление
            node.removeChildren(leafKey!!)

            if (node.children.isEmpty() && !node.isRoot) { // Узел теперь пуст. Рекурсивно удалить
                this.setTranslation(fullPath, null)
            }
            return
        }

        // Создание или перезапись
        node.setChildren(leafKey!!, translation)
    }

    val fullKeys: Set<KeyPath>
        /**
         * @return Все ключи перевода в виде абсолютных путей (full-key)
         */
        get() = this.getFullKeys(KeyPath(), this.rootNode) // Просто используем корневой узел

    /**
     * @param parentPath Родительский путь ключа
     * @param node Узел, с которого начинается поиск
     * @return Все ключи перевода, где путь содержит указанный @parentPath
     */
    fun getFullKeys(parentPath: KeyPath, node: TranslationNode): Set<KeyPath> {
        val keys: MutableSet<KeyPath> = LinkedHashSet()

        if (node.isLeaf) { // Этот узел не ведёт к дочерним элементам - просто добавить ключ
            keys.add(parentPath)
        }

        for ((key, value) in node.children) {
            keys.addAll(this.getFullKeys(KeyPath(parentPath, key), value))
        }

        return keys
    }

    val isSorting: Boolean
        get() = rootNode.children is TreeMap<*, *>

    override fun toString(): String {
        return "TranslationData{" +
                "mapClass=" + rootNode.children.javaClass.simpleName +
                ", locales=" + locales +
                ", rootNode=" + rootNode +
                '}'
    }
}