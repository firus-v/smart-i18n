package com.github.firusv.smarti18n.exception

/**
 * Указывает, что каталог перевода еще не настроен.
 * @author firus-v
 */
class EmptyLocalesDirException(message: String?) : IllegalArgumentException(message)