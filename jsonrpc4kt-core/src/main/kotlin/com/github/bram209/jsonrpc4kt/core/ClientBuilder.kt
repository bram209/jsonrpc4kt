package com.github.bram209.jsonrpc4kt.core

fun jsonRpcClient(host: String, port: Int, init: JsonRpcClient.() -> Unit): JsonRpcClient {
    val jsonRpcClient = JsonRpcClient(host, port)
    jsonRpcClient.init()
    return jsonRpcClient
}


fun JsonRpcClient.authentication(init: Authentication.() -> AuthMode): Authentication {
    auth.authMode = auth.init()
    return auth
}

fun JsonRpcClient.conversion(init: Conversion.() -> Converter): Conversion {
    conv.converter = conv.init()
    return conv
}

fun Authentication.basicAuth(username: String, password: String) = BasicAuth(username, password)
