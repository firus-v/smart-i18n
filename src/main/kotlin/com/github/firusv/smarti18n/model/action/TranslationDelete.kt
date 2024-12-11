package com.github.firusv.smarti18n.model.action

import com.github.firusv.smarti18n.model.Translation


/**
 * Представляет собой запрос на обновление для удаления существующего перевода.
 * @author firus-v
 */
class TranslationDelete(translation: Translation) :
    TranslationUpdate(translation, null)