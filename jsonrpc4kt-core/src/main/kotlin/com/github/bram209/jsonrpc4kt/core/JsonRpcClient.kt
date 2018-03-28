package com.github.bram209.jsonrpc4kt.core

import com.eclipsesource.json.JsonObject
import com.github.kittinunf.fuel.httpPost

internal data class RpcCall(val method: String, val params: Array<out Any>, val id: Int?) {
    val jsonrpc = "2.0"
}

internal data class RpcResult<T>(val result: T?, val error: RpcError?) {
    fun success() = error == null
    fun get() = result!!
}

data class RpcError(val code: Int, val message: String)

class JsonRpcClient(host: String, port: Int) {
    internal var auth = Authentication()
    internal var conv = Conversion()

    private val url = "http://$host:$port"

    fun makeRequest(method: String, id: Int? = null, vararg params: Any): JsonObject {
        return makeRequest(method, id, params, returnType = JsonObject::class.java)
    }

    inline fun <reified T> makeRequest(method: String, id: Int? = null, vararg params: Any): T {
        return makeRequest(method, id, params, returnType = T::class.java)
    }

    fun <T> makeRequest(method: String, id: Int? = null, vararg params: Any, returnType: Class<T>): T {
        val rpcReq = RpcCall(method, params, id)
        val request = url.httpPost().body(conv.convertFromRequest(rpcReq))
        if (auth.authMode is BasicAuth) {
            val basicAuth = auth.authMode as BasicAuth
            request.authenticate(basicAuth.username, basicAuth.password)
        }

        val (_, response, result) = request.response()
        val (data, error) = result

        if (error == null && data != null) {
            val rpcResult = conv.convertToResult(data, returnType)
            if (rpcResult.success()) {
                return rpcResult.get()
            }

            TODO("Handle error ${rpcResult.error!!.message}")
        }

        throw Exception(error!!.message)
    }
}


