package com.github.firusv.smarti18n.io.parser.json

import com.github.firusv.smarti18n.io.parser.ArrayMapper
import com.google.gson.*

object JsonArrayMapper : ArrayMapper() {

    private val GSON: Gson = GsonBuilder().create()

    fun read(array: JsonArray): String {
        return read(array.iterator()) { jsonElement ->
            if (jsonElement.isJsonArray || jsonElement.isJsonObject) {
                "\\$jsonElement"
            } else {
                jsonElement.asString
            }
        }
    }

    fun write(concat: String): JsonArray {
        val array = JsonArray()

        write(concat) { element ->
            if (element.startsWith("\\")) {
                array.add(GSON.fromJson(element.replace("\\", ""), JsonElement::class.java))
            } else {
                array.add(element)
            }
        }

        return array
    }
}