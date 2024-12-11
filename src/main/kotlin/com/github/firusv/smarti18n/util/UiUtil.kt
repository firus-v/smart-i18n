package com.github.firusv.smarti18n.util

object UiUtil {
    /**
     * Создает строку, совместимую с HTML, которая показывает все определенные переводы
     * @param messages Содержит локали с желаемым переводом
     * @return Строка с форматом HTML
     */
    fun generateHtmlTooltip(messages: Set<Map.Entry<String?, String?>>): String {
        val builder = StringBuilder()

        builder.append("<html>")

        for ((key, value) in messages) {
            builder.append("<b>")
            builder.append(key).append(":")
            builder.append("</b> ")
            builder.append(value)
            builder.append("<br>")
        }

        builder.append("</html>")

        return builder.toString()
    }
}