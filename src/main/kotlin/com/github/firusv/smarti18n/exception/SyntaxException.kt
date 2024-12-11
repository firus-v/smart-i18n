package com.github.firusv.smarti18n.exception

import com.github.firusv.smarti18n.model.TranslationFile

/**
 * Указывает на синтаксическую ошибку в управляемом файле перевода.
 * @author firus-v
 */
class SyntaxException(message: String?, val file: TranslationFile) : RuntimeException(message)