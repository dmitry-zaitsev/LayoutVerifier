package com.redapparat.layoutverifier.serializer

import java.io.InputStream
import java.io.OutputStream

internal interface Serializer {

    fun serializeToStream(entity: Map<String, Any>, stream: OutputStream)

    fun deserializeFromStream(stream: InputStream): Map<String, *>

    fun toPrettyJson(entity: Any): String

}