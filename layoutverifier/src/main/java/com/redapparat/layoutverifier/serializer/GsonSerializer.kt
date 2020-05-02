package com.redapparat.layoutverifier.serializer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.*

class GsonSerializer(
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()
) : Serializer {

    override fun serializeToStream(entity: Map<String, Serializable>, stream: OutputStream) {
        OutputStreamWriter(stream).use {
            gson.toJson(entity, it)
        }
    }

    override fun deserializeFromStream(stream: InputStream): Map<String, *> {
        return InputStreamReader(stream).use {
            gson.fromJson(
                it,
                object : TypeToken<Map<String, *>>() {}.type
            )
        }
    }

    override fun toPrettyJson(entity: Any): String {
        return gson.toJson(entity)
    }
}