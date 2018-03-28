package com.github.bram209.jsonrpc4kt.core

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.JsonValue
import java.io.*

class Conversion {
    internal var converter: Converter = DefaultConverter

    internal fun <T> convertToResult(input: ByteArray, returnType: Class<T>): RpcResult<T> {
        val jsonObject = Json.parse(String(input)).asObject()
        if (!jsonObject["error"].isNull) {
            val errorNode = jsonObject["error"].asObject()
            return RpcResult(null, RpcError(errorNode["code"].asInt(), errorNode["message"].asString()))
        }

        val resultNode = jsonObject["result"]
        return RpcResult(converter.deserialize(resultNode.toString(), returnType), null)
    }

    internal fun convertFromRequest(rpcCall: RpcCall): ByteArray {
        val jsonObject = JsonObject()
        jsonObject.add("jsonrpc", rpcCall.jsonrpc)
        jsonObject.add("method", rpcCall.method)
        jsonObject.add("params", Json.parse(converter.serialize(rpcCall.params)))
        if (rpcCall.id != null) {
            jsonObject.add("id", rpcCall.id)
        }

        return jsonObject.toByteArray()
    }

    fun JsonValue.toByteArray(): ByteArray {
        val bos = ByteArrayOutputStream()
        OutputStreamWriter(bos).use { writeTo(it) }
        return bos.toByteArray()
    }
}

interface Converter {
    fun <T> deserialize(input: String, returnType: Class<T>): T?
    fun serialize(input: Any): String
}

object DefaultConverter : Converter {

    @Suppress("UNCHECKED_CAST")
    override fun <T> deserialize(input: String, returnType: Class<T>): T? {
        if (JsonValue::class.java.isAssignableFrom(returnType)) {
            val jsonValue = Json.parse(input)
            return jsonValue as T
        }


        throw Exception("The default converter can only convert to Json types in the ${JsonValue::class.java.`package`.name} package.")
    }

    override fun serialize(input: Any): String {
        val jsonValue = serializeToJsonValue(input)
        return jsonValue.toString()
    }

    private fun serializeToJsonValue(input: Any): JsonValue? = when (input) {
        is Array<*> -> {
            val array = JsonArray()
            for (elem in input) array.add(serializeToJsonValue(elem!!))
            array
        }
        is Int -> Json.value(input)
        is Long -> Json.value(input)
        is Float -> Json.value(input)
        is Double -> Json.value(input)
        is Boolean -> Json.value(input)
        is String -> Json.value(input)
        else -> null
    }
}