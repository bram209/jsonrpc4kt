package com.github.bram209.jsonrpc4kt.core

fun jsonRpcClient(host: String, port: Int, init: JsonRpcClient.() -> Unit): JsonRpcClient {
    val jsonRpcClient = JsonRpcClient(host, port)
    jsonRpcClient.init()
    return jsonRpcClient
}


fun JsonRpcClient.authentication(init: Authentication.() -> Unit): Authentication {
    authentication.init()
    return authentication
}

fun JsonRpcClient.convertion(init: Conversion.() -> Unit): Conversion {
    convertion.init()
    return convertion
}
