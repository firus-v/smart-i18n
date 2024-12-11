package com.github.firusv.smarti18n.util

import java.io.StringWriter
import java.util.regex.Pattern

/**
 * Утилиты для работы со строками.
 */
object StringUtil {

    /**
     * Проверяет, является ли переданная строка шестнадцатеричным числом.
     * Например: {@code 0x100...}, {@code -0x100...} и {@code +0x100...}.
     * @param string Строка для проверки
     * @return true, если строка представляет собой шестнадцатеричное число, иначе false
     */
    fun isHexString(string: String): Boolean {
        val hexNumberPattern = Pattern.compile("[+-]?0[xX][0-9a-fA-F]+")
        return hexNumberPattern.matcher(string).matches()
    }

    /**
     * Экранирует управляющие символы в переданной строке.
     * Вдохновлено Apache Commons (см. {@link org.apache.commons.text.StringEscapeUtils}).
     * @param input Входная строка
     * @param skipStrings Нужно ли пропускать строковые литералы ("", '')? (Необходимо, например, для JSON)
     * @return Экранированная строка
     */
    fun escapeControls(input: String, skipStrings: Boolean): String {
        val length = input.length
        val out = StringWriter(length * 2)

        for (i in 0 until length) {
            val ch = input[i]

            if (ch < ' ') {
                when (ch) {
                    '\b' -> {
                        out.write(92)
                        out.write(98)
                    }
                    '\t' -> {
                        out.write(92)
                        out.write(116)
                    }
                    '\n' -> {
                        out.write(92)
                        out.write(110)
                    }
                    '\u000b' -> {
                        if (ch > 15.toChar()) {
                            out.write("\\u00" + hex(ch))
                        } else {
                            out.write("\\u000" + hex(ch))
                        }
                    }
                    '\u000c' -> {
                        out.write(92)
                        out.write(102)
                    }
                    '\r' -> {
                        out.write(92)
                        out.write(114)
                    }
                    else -> {
                        if (ch > 15.toChar()) {
                            out.write("\\u00" + hex(ch))
                        } else {
                            out.write("\\u000" + hex(ch))
                        }
                    }
                }
            } else {
                when (ch) {
                    '"' -> {
                        if (!skipStrings) {
                            out.write(92)
                        }
                        out.write(34)
                    }
                    '\'' -> {
                        if (!skipStrings) {
                            out.write(92)
                        }
                        out.write(39)
                    }
                    '/' -> {
                        out.write(92)
                        out.write(47)
                    }
                    '\\' -> {
                        out.write(92)
                        out.write(92)
                    }
                    else -> {
                        out.write(ch.toInt())
                    }
                }
            }
        }

        return out.toString()
    }

    private fun hex(ch: Char): String {
        return Integer.toHexString(ch.toInt()).toUpperCase()
    }
}