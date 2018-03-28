package com.github.bram209.jsonrpc4kt.service

import com.github.bram209.jsonrpc4kt.core.JsonRpcClient
import java.lang.reflect.Proxy

annotation class JsonRpcMethodName(val method: String)

interface JsonRpcService

inline fun <reified T : JsonRpcService> JsonRpcClient.createService(): T {
    val javaClass = T::class.java
    assert(javaClass.isInterface)

    val proxy = Proxy.newProxyInstance(javaClass.classLoader, arrayOf(javaClass), { proxy, method, args ->
        val rpcMethod = if (method.isAnnotationPresent(JsonRpcMethodName::class.java)) {
            method.getAnnotation(JsonRpcMethodName::class.java).method
        } else {
            method.name.toLowerCase()
        }

        if (args != null) {
            makeRequest(rpcMethod, params = *args, returnType = method.returnType)
        } else {
            makeRequest(rpcMethod, returnType = method.returnType)
        }
    })

    return proxy as T
}
