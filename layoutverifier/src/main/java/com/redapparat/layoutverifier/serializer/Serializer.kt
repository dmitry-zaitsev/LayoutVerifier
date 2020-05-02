package com.redapparat.layoutverifier.serializer

import java.io.InputStream
import java.io.OutputStream
import java.io.Serializable

internal interface Serializer {

    fun serializeToStream(entity: Map<String, Serializable>, stream: OutputStream)

    fun deserializeFromStream(stream: InputStream): Map<String, *>

    fun toPrettyJson(entity: Any): String

}