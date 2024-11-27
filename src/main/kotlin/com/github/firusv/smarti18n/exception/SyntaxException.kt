package com.github.firusv.smarti18n.exception

import com.github.firusv.smarti18n.model.TranslationFile

/**
 * Indicates a syntax error in a managed translation file.
 * @author marhali
 */
class SyntaxException(message: String?, val file: TranslationFile) : RuntimeException(message)