package com.github.bram209.jsonrpc4kt.gson

import com.github.bram209.jsonrpc4kt.core.Conversion
import com.github.bram209.jsonrpc4kt.core.Converter
import com.google.gson.Gson

fun Conversion.gson(init: Gson.() -> Unit): JsonConverter {
    val gson = Gson()
    gson.init()
    return JsonConverter(gson)
}

class JsonConverter(val gson: Gson) : Converter {
    override fun <T> deserialize(input: String, returnType: Class<T>): T? = gson.fromJson(input, returnType)
    override fun serialize(input: Any): String = gson.toJson(input)
}
