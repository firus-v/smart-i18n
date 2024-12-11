package com.github.firusv.smarti18n.util

import org.apache.commons.io.FilenameUtils


/**
 * Утилита для регулярных выражений.
 * @author firus-v
 */
object WildcardRegexMatcher {
    fun matchWildcardRegex(string: String, pattern: String): Boolean {
        val wildcardMatch = FilenameUtils.wildcardMatchOnSystem(string, pattern)

        if (wildcardMatch) {
            return true
        }

        return try {
            string.matches(pattern.toRegex())
        } catch (e: Exception) {
            false
        }
    }
}