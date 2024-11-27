package com.github.firusv.smarti18n.exception

/**
 * Indicates that the translation's directory has not been configured yet
 * @author marhali
 */
class EmptyLocalesDirException(message: String?) : IllegalArgumentException(message)