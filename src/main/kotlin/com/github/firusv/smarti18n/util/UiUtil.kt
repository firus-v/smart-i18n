package com.github.firusv.smarti18n.util

object UiUtil {
    /**
     * Generates a html compliant string which shows all defined translations
     * @param messages Contains locales with desired translation
     * @return String with html format
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