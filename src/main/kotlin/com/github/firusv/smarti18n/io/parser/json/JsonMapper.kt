package com.github.firusv.smarti18n.io.parser.json

import com.github.firusv.smarti18n.model.TranslationNode
import com.github.firusv.smarti18n.model.TranslationValue
import com.github.firusv.smarti18n.util.StringUtil
import com.google.gson.*
import groovy.json.StringEscapeUtils
import org.apache.commons.lang3.math.NumberUtils

/**
 * Маппер для преобразования JSON-объектов в узлы перевода и обратно.
 * @author firus-v
 */
object JsonMapper {

    fun read(locale: String, json: JsonObject, node: TranslationNode) {
        for ((key, value) in json.entrySet()) {
            val childNode = node.getOrCreateChildren(key)

            if (value.isJsonObject) {
                // Вложенный элемент - рекурсивный вызов
                read(locale, value.asJsonObject, childNode)
            } else {
                val translation = childNode.value ?: TranslationValue()
                val content = if (value.isJsonArray) {
                    JsonArrayMapper.read(value.asJsonArray)
                } else {
                    StringUtil.escapeControls(value.asString, true)
                }

                translation.put(locale, content)
                childNode.setValue(translation)
            }
        }
    }

    fun write(locale: String, json: JsonObject, node: TranslationNode) {
        for ((key, childNode) in node.children) {
            if (!childNode.isLeaf) {
                // Вложенный узел - рекурсивный вызов
                val childJson = JsonObject()
                write(locale, childJson, childNode)
                if (childJson.size() > 0) {
                    json.add(key, childJson)
                }
            } else {
                val translation = childNode.value
                val content = translation?.get(locale)

                if (content != null) {
                    when {
                        JsonArrayMapper.isArray(content) -> {
                            json.add(key, JsonArrayMapper.write(content))
                        }
                        NumberUtils.isCreatable(content) -> {
                            json.add(key, JsonPrimitive(NumberUtils.createNumber(content)))
                        }
                        else -> {
                            json.add(key, JsonPrimitive(StringEscapeUtils.unescapeJava(content)))
                        }
                    }
                }
            }
        }
    }
}