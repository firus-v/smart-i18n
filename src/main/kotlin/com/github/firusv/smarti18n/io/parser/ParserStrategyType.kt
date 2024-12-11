package com.github.firusv.smarti18n.io.parser

import com.github.firusv.smarti18n.io.parser.json.JsonParserStrategy


/**
 * Представляет все поддерживаемые стратегии парсера файлов.
 * @author firus-v
 */
enum class ParserStrategyType(private val strategy: Class<out ParserStrategy>) {

    JSON(JsonParserStrategy::class.java),
    ARB(JsonParserStrategy::class.java);

    /**
     * Получает стратегию парсера, соответствующую типу.
     */
    fun getStrategy(): Class<out ParserStrategy> {
        return strategy
    }

    /**
     * Возвращает расширение файла, соответствующее типу стратегии.
     */
    fun getFileExtension(): String {
        return name.toLowerCase()
    }

    /**
     * Возвращает шаблон примера для файлов, соответствующих стратегии.
     */
    fun getExampleFilePattern(): String {
        return "*.${this.getFileExtension()}"
    }

    /**
     * Преобразует стратегию в индекс.
     */
    fun toIndex(): Int {
        return values().indexOf(this).takeIf { it >= 0 } ?: throw NullPointerException()
    }

    companion object {
        /**
         * Возвращает стратегию по индексу.
         */
        fun fromIndex(index: Int): ParserStrategyType {
            return values()[index]
        }
    }
}