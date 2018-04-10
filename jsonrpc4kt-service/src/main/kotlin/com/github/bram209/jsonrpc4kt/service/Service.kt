package com.github.bram209.jsonrpc4kt.service

import com.github.bram209.jsonrpc4kt.core.JsonRpcClient
import java.lang.reflect.Proxy

annotation class JsonRpcMethodName(val method: String)
annotation class JsonRpcMethodNameTransformation(val transformation: MethodNameTransformation)

interface JsonRpcService

enum class MethodNameTransformation(private  val methodFun: (String) -> String) {
    TO_LOWER_CASE(String::toLowerCase),
    TO_UPPER_CASE(String::toUpperCase),
    TO_SNAKE_CASE({ name -> name.filter { it.isUpperCase() }.fold(name) { name, char -> name.replace(char.toString(), "_$char") } });

    fun transform(name: String) = methodFun(name)
}

inline fun <reified T : JsonRpcService> JsonRpcClient.createService(): T {
    val javaClass = T::class.java
    assert(javaClass.isInterface)

    val globalNameTransformAnnotation = javaClass.getAnnotation(JsonRpcMethodNameTransformation::class.java)

    val proxy = Proxy.newProxyInstance(javaClass.classLoader, arrayOf(javaClass), { proxy, method, args ->
        val rpcMethod = if (method.isAnnotationPresent(JsonRpcMethodName::class.java)) {
            method.getAnnotation(JsonRpcMethodName::class.java).method
        } else {
            val nameTransformationAnnotation = method.getAnnotation(JsonRpcMethodNameTransformation::class.java)
            when {
                nameTransformationAnnotation != null -> nameTransformationAnnotation.transformation.transform(method.name)
                globalNameTransformAnnotation != null -> globalNameTransformAnnotation.transformation.transform(method.name)
                else -> method.name
            }
        }

        if (args != null) {
            makeRequest(rpcMethod, params = *args, returnType = method.returnType)
        } else {
            makeRequest(rpcMethod, returnType = method.returnType)
        }
    })

    return proxy as T
}
