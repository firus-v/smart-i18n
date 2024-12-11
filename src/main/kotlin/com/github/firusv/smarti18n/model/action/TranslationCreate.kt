package com.github.firusv.smarti18n.model.action

import com.github.firusv.smarti18n.model.Translation


/**
 * Представляет собой запрос на обновление для создания нового перевода.
 * @author firus-v
 */
class TranslationCreate(translation: Translation) :
    TranslationUpdate(null, translation)