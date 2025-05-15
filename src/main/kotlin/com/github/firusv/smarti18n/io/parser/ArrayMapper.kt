package com.github.firusv.smarti18n.io.parser


import com.github.firusv.smarti18n.util.StringUtil
import groovy.json.StringEscapeUtils
import java.text.MessageFormat
import java.util.function.Consumer
import java.util.function.Function
import java.util.regex.Pattern

/**
 * Простой механизм для работы с массивами значений перевода.
 * Некоторые системы i18n позволяют пользователю определять массивы значений для некоторых переводов.
 * Мы поддерживаем массивы, оборачивая их в: '!arr[valueA;valueB]'.
 * @author firus-v
 */
abstract class ArrayMapper {

    val PREFIX = "!arr["
    val SUFFIX = "]"
    val DELIMITER = ';'

    // Регулярное выражение для разделения массива по разделителю
    val SPLITERATOR_REGEX = MessageFormat.format("(?<!\\\\){0}", Pattern.quote(DELIMITER.toString()))

    /**
     * Чтение массива значений и его преобразование в строку.
     * @param elements Итератор для элементов массива
     * @param stringFactory Функция для преобразования элемента в строку
     * @return Строковое представление массива значений
     */
    protected fun <T> read(elements: Iterator<T>, stringFactory: Function<T, String>): String {
        val builder = StringBuilder(PREFIX)
        var i = 0

        while (elements.hasNext()) {
            if (i > 0) {
                builder.append(DELIMITER)
            }

            val value = stringFactory.apply(elements.next())

            builder.append(
                StringUtil.escapeControls(
                    value.replace(DELIMITER.toString(), "\\" + DELIMITER), true
                )
            )
            i++
        }

        builder.append(SUFFIX)
        return builder.toString()
    }

    /**
     * Запись строкового представления массива и вызов функции для каждого элемента.
     * @param concat Строка, представляющая массив значений
     * @param writeElement Функция для записи каждого элемента
     */
    protected fun write(concat: String, writeElement: Consumer<String>) {
        val data = concat.substring(PREFIX.length, concat.length - SUFFIX.length)
        data.split(DELIMITER).forEach { element ->
            val unescapedElement = element.replace("\\" + DELIMITER, DELIMITER.toString())
            writeElement.accept(StringEscapeUtils.unescapeJava(unescapedElement))
        }
    }

    /**
     * Проверяет, является ли строка представлением массива.
     * @param concat Строка для проверки
     * @return true, если строка представляет собой массив, иначе false
     */
    fun isArray(concat: String?): Boolean {
        return concat != null && concat.startsWith(PREFIX) && concat.endsWith(SUFFIX)
    }
}