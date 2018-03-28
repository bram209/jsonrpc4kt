package com.github.bram209.jsonrpc4kt.core

import com.eclipsesource.json.JsonObject
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.httpPost

internal data class RpcCall(val method: String, val params: Array<out Any>, val id: Int?) {
    val jsonrpc = "2.0"
}

internal data class RpcResult<T>(val result: T?, val error: Error?) {
    fun success() = error == null
    fun get() = result!!
}

class JsonRpcClient(val host: String, val port: Int) {
    internal var authentication = Authentication()
    internal var convertion = Conversion()

    private val url = "http://$host:$port"

    fun makeRequest(method: String, id: Int? = null, vararg params: Any): JsonObject {
        return makeRequest(method, id, params, returnType = JsonObject::class.java)
    }

    inline fun <reified T> makeRequest(method: String, id: Int? = null, vararg params: Any): T {
        return makeRequest(method, id, params, returnType = T::class.java)
    }

    fun <T> makeRequest(method: String, id: Int? = null, params: Array<out Any>, returnType: Class<T>): T {
//        val mapper = jacksonObjectMapper()
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val rpcReq = RpcCall(method, params, id)
        val request = url.httpPost().body(convertion.convertFromRequest(rpcReq))
        if (authentication.authMode is BasicAuth) {
            val basicAuth = authentication.authMode as BasicAuth
            request.authenticate(basicAuth.username, basicAuth.password)
        }

        val (_, response, result) = request.response()
        val (data, error) = result

        if (error == null && data != null) {
            val rpcResult = convertion.convertToResult(data, returnType)
            if (rpcResult.success()) {
                return rpcResult.get()
            }

            TODO("Handle error ${rpcResult.error!!.message}")
        }

        TODO("Deserialize and throw error with code and message")
    }
}


fun Conversion.jackson(init: ObjectMapper.() -> Unit): ObjectMapper {
    val mapper = jacksonObjectMapper()
    mapper.init()
    return mapper
}


//object DefaultJacksonConverter : Converter {
//    private val mapper = jacksonObjectMapper()
//    override fun <T> deserialize(input: ByteArray, clazz: Class<T>): T? = mapper.readValue(input, clazz)
//    override fun <T> serialize(input: T): ByteArray = mapper.writeValueAsBytes(input)
//}


data class Error(val code: Int, val message: String)

